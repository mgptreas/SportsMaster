import random
from django.db.models import Avg
from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .models import *
from .serializers import *
from django.utils import timezone
from django.db import transaction



################################################ REGISTER/LOGIN USER ######################################################################################################
#Get all users
@api_view(['GET'])
def get_all_users(request):
    users = Users.objects.all()
    users_data = [{"username": user.username, "email": user.email} for user in users]
    return Response(users_data)

#Get a user profile information, used to be called by other functions.
def get_user_profile(user):
    user_data = {
        "uID": int(user.uID),
        "username": user.username,
        "email": user.email,
        "name": user.name,
        "height": user.height,
        "weight": user.weight,
        "points": user.points,
        "premium": user.premium
    }
    return user_data

#Register a User
@api_view(['POST'])
def register_user(request):
    data = request.data
    
    username = data.get('username')
    email = data.get('email')
    
    if Users.objects.filter(username=username).exists():
        return Response({"error": "Username already exists"}, status=status.HTTP_400_BAD_REQUEST)
    
    if Users.objects.filter(email=email).exists():
        return Response({"error": "Email already exists"}, status=status.HTTP_400_BAD_REQUEST)
    
    # Set default values for points and premium
    user_data = {
        "username": data.get('username'),
        "email": data.get('email'),
        "name": data.get('name'),
        "height": data.get('height'),
        "weight": data.get('weight'),
        "points": 50,  # default points
        "premium": False  # default premium status
    }
    
    user_serializer = UserSerializer(data=user_data)
    
    if user_serializer.is_valid():
        user = user_serializer.save()

        unlock_exercises_for_user(user) #Unlock exercises for user

        user_auth_data = {
            'uID': user.uID,
            'username': data.get('username'),
            'password': data.get('password')
        }
        user_auth_serializer = UserAuthSerializer(data=user_auth_data)
        
        if user_auth_serializer.is_valid():
            user_auth_serializer.save()
            return Response(user_serializer.data, status=status.HTTP_201_CREATED)
        else:
            user.delete()  # Rollback user creation if user auth is invalid
            return Response(user_auth_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    
    return Response(user_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

#Exercises that will be unlocked when a user registers
def unlock_exercises_for_user(user):
    # Define exercise IDs to be unlocked
    exercise_ids = [1, 2, 3, 4, 5]  #eID that will be unlocked

    # Loop through exercise_ids and create UnlockedExercises objects for each exercise
    for exercise_id in exercise_ids:
        unlocked_exercise = Unlocked.objects.create(user=user, exercise_id=exercise_id)
        unlocked_exercise.save()

#Check Login information
@api_view(['GET'])
def login_user(request):
    username = request.query_params.get('username')
    password = request.query_params.get('password')
    
    if not username or not password:
        return Response({"error": "Username and password are required"}, status=status.HTTP_400_BAD_REQUEST)
    
    try:
        user_auth = UserAuth.objects.get(username=username, password=password)
        user = Users.objects.get(username=username)
        user_data = get_user_profile(user)
        return Response(user_data, status=status.HTTP_200_OK)
    except UserAuth.DoesNotExist:
        return Response({"error": "Invalid credentials"}, status=status.HTTP_401_UNAUTHORIZED)
    
###################################### SELECT FIELDS FOR WORKOUT ###########################################################################################################

@api_view(['GET'])
def get_sport_fields(request):
    sport_name = request.query_params.get('sport_name')

    if not sport_name:
        return Response({"error": "sport_name is a required parameter."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        sport = Sports.objects.get(name=sport_name)
    except Sports.DoesNotExist:
        return Response({"error": "Sport not found."}, status=status.HTTP_404_NOT_FOUND)

    # Get the fields of the sport as a list of strings (excluding nulls and empty strings)
    fields = [
        field for field in [
            sport.field1, 
            sport.field2, 
            sport.field3, 
            sport.field4, 
            sport.field5
        ]
        if field and field.strip()  # Check for both None and empty strings
    ]

    return Response({"fields": fields})

####################################### SELECT EXERCISES FOR WORKOUT #######################################################################################################

@api_view(['GET'])
def select_workout(request):
    user_id = request.query_params.get('uID')
    sport_name = request.query_params.get('sport_name')
    selected_fields_str = request.query_params.get('fields')
    available_time = request.query_params.get('time')

    print(f"Received parameters - user_id: {user_id}, sport_name: {sport_name}, selected_fields: {selected_fields_str}, available_time: {available_time}")

    if not (user_id and sport_name and selected_fields_str and available_time):
        return Response({"error": "All fields are required"}, status=status.HTTP_400_BAD_REQUEST)

    selected_fields = [field.strip() for field in selected_fields_str.split(',')] #Remove whitespace around fields
    print(f"Split selected_fields: {selected_fields}")
    
    available_time = int(available_time)

    try:
        sport = Sports.objects.get(name=sport_name)
        print(f"Found sport: {sport}")
    except Sports.DoesNotExist:
        return Response({"error": "Sport not found"}, status=status.HTTP_404_NOT_FOUND)

    field_map = {
        sport.field1: 1,
        sport.field2: 2,
        sport.field3: 3,
        sport.field4: 4,
        sport.field5: 5
    }
    print(f"Field map: {field_map}")

    try:
        selected_field_indices = [field_map[field] for field in selected_fields]
        print(f"Selected field indices: {selected_field_indices}")
    except KeyError as e:
        return Response({"error": f"Field '{str(e)}' not found for the selected sport"}, status=status.HTTP_404_NOT_FOUND)

    selected_exercises = select_exercises(user_id, sport.sID, selected_field_indices, available_time)
    print(f"Selected exercises: {selected_exercises}")
    return Response(selected_exercises, status=status.HTTP_200_OK)

def select_exercises(user_id, sport_id, selected_fields, available_time):
    exercises = fetch_unlocked_exercises(user_id, sport_id)
    print(f"Fetched exercises: {exercises}")
    aggregated_data = {agg.eID.eID: agg for agg in Aggregated.objects.filter(uID=user_id)}
    print(f"Aggregated data: {aggregated_data}")
    user_instance = Users.objects.get(uID=user_id) # Fetch the user instance
    print(f"User instance: {user_instance}")
    sorted_exercises = sort_exercises(exercises, selected_fields, aggregated_data)
    print(f"Sorted exercises: {sorted_exercises}")

    # Filter out exercises with a score of 0 in any selected field
    sorted_exercises = [
        (exercise, score) for exercise, score in sorted_exercises
        if all(getattr(exercise, f"field{i+1}") > 0 for i in selected_fields)
    ]
    
    selected_exercises = []
    total_time = 0
    
    for exercise, score in sorted_exercises:
        avgTOC = aggregated_data.get(exercise.eID, Aggregated(uID=user_instance, eID=exercise, avgTOC=5)).avgTOC  # Default to 5 minutes if avgTOC is not available
        if total_time + avgTOC <= available_time:
            selected_exercises.append({
                "eID": exercise.eID,
                "description": exercise.description,
                "avgTOC": avgTOC,
                "score": score
            })
            total_time += avgTOC
    
    print(f"Final selected exercises: {selected_exercises}")
    return selected_exercises

def fetch_unlocked_exercises(user_id, sport_id):
    unlocked_exercises = Unlocked.objects.filter(uID=user_id).values_list('eID', flat=True)
    exercises = Exercises.objects.filter(eID__in=unlocked_exercises, sID=sport_id)
    print(f"Fetched unlocked exercises: {exercises}")
    return exercises

def sort_exercises(exercises, selected_fields, aggregated_data):
    exercise_scores = []
    for exercise in exercises:
        score = calculate_exercise_score(exercise, selected_fields, aggregated_data)
        exercise_scores.append((exercise, score))
    
    sorted_exercises = sorted(exercise_scores, key=lambda x: x[1], reverse=True)
    print(f"Exercise scores: {exercise_scores}")
    return sorted_exercises

def calculate_exercise_score(exercise, selected_fields, aggregated_data):
    weights = [10 if i + 1 in selected_fields else 1 for i in range(5)]
    field_scores = sum([getattr(exercise, f'field{i+1}') * weights[i] for i in range(5)])
    
    main_field_index = selected_fields[0] - 1
    if getattr(exercise, f'field{selected_fields[0]}') == 0:
        print(f"Main field is 0 for exercise {exercise.eID}. Setting score to 0.")
        return 0
    
    agg = aggregated_data.get(exercise.eID)

    if not agg:
        print(f"No aggregated data found for exercise {exercise.eID}. Assigning high score.")
        avgChallenging = 4 # In order to be +0
        avgFeedback = 5 # In order to be +0
        commonness = 0  # Do not affect the score
        rarity = 0 # Increase the score in order for exercise to be selected for the first time
    else:
        avgChallenging = agg.avgChallenging
        avgFeedback = agg.avgFeedback
        commonness = agg.commonness
        rarity = agg.rarity

    
    challenging_adjustment = get_challenging_adjustment(avgChallenging)
    feedback_adjustment = get_feedback_adjustment(avgFeedback)
    
    final_score = (
        field_scores +
        challenging_adjustment +
        feedback_adjustment -
        commonness +
        rarity 
        +random.randint(-10, 10)
    )
    print(f"Final score for exercise {exercise.eID}: {final_score}")
    return final_score

def get_challenging_adjustment(avgChallenging):
    if avgChallenging < 2:
        return -20
    elif avgChallenging < 4:
        return -10
    elif avgChallenging == 4:
        return 0
    elif avgChallenging == 5:
        return 10
    elif avgChallenging <= 7:
        return 20
    elif avgChallenging < 8:
        return -10
    else:
        return -20

def get_feedback_adjustment(avgFeedback):
    if avgFeedback < 2:
        return -30
    elif avgFeedback < 4:
        return -20
    elif avgFeedback  <6:
        return 0
    elif avgFeedback < 8:
        return 10
    elif avgFeedback <= 9:
        return 20
    else:
        return 30
    


############################ USER STATISTICS ##############################################################################################################################
@api_view(['GET'])
def get_user_exercise_stats(request):
    user_id = request.query_params.get('uID')
    print(f"Received parameters - user_id: {user_id}")

    if not user_id:
        return Response({"error": "uID is a required parameter."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        user = Users.objects.get(uID=user_id)
    except Users.DoesNotExist:
        return Response({"error": "User not found."}, status=status.HTTP_404_NOT_FOUND)

    aggregated_data = Aggregated.objects.filter(uID=user_id)
    if not aggregated_data.exists():
        return Response({"error": "No aggregated data found for the user."}, status=status.HTTP_404_NOT_FOUND)

    exercise_stats = []
    for agg in aggregated_data:
        exercise = Exercises.objects.get(eID=agg.eID.eID)
        exercise_stats.append({
            "exercise_name": exercise.name,
            "description": exercise.description,
            "avgTOC": agg.avgTOC,
            "avgChallenging": agg.avgChallenging,
            "avgFeedback": agg.avgFeedback,
            "CoT": agg.CoT
        })

    return Response(exercise_stats, status=status.HTTP_200_OK)


############################# FETCH UNLOCKED #############################################################################################################################
@api_view(['GET'])
def check_unlocked(request):
    user_id = request.query_params.get('user_id')
    
    if not user_id:
        return Response({"error": "user_id is a required parameter."}, status=status.HTTP_400_BAD_REQUEST)
    
    unlocked_exercises = Unlocked.objects.filter(uID=user_id)
    
    if not unlocked_exercises.exists():
        return Response({"error": "No unlocked exercises found for the user."}, status=status.HTTP_404_NOT_FOUND)
    
    serializer = UnlockedSerializer(unlocked_exercises, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
def get_exercises_with_sport(request):
    user_id = request.query_params.get('uID')

    if not user_id:
        return Response({"error": "uID is a required parameter."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        user = Users.objects.get(uID=user_id)
    except Users.DoesNotExist:
        return Response({"error": "User not found."}, status=status.HTTP_404_NOT_FOUND)

    # Get unlocked exercise IDs for the user
    unlocked_exercise_ids = Unlocked.objects.filter(uID=user).values_list('eID', flat=True)

    # Get all exercises and their associated sport names
    exercises = Exercises.objects.select_related('sID') 

    # Create a list of dictionaries to hold the serialized exercise data
    serialized_exercises = []
    for exercise in exercises:
        sport_name = exercise.sID.name if exercise.sID else None  # Get sport name or None if exercise has no sport
        is_unlocked = exercise.eID in unlocked_exercise_ids   # Check if exercise is unlocked for user

        # Serialize the exercise data
        serialized_exercise = ExercisesSerializer(exercise).data
        serialized_exercise['sport_name'] = sport_name
        serialized_exercise['is_unlocked'] = is_unlocked

        serialized_exercises.append(serialized_exercise)

    return Response(serialized_exercises) 


########################################### GET EXERCISE INFO ######################################################################################
@api_view(['GET'])
def get_exercise_info(request):
    exercise_id = request.query_params.get('eID')

    if not exercise_id:
        return Response({"error": "eID is a required parameter."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        exercise = Exercises.objects.select_related('sID').get(eID=exercise_id)
        sport = exercise.sID
    except Exercises.DoesNotExist:
        return Response({"error": "Exercise not found."}, status=status.HTTP_404_NOT_FOUND)

    # Serialize the exercise data
    serialized_exercise = ExercisesSerializer(exercise).data

    # Add sport field names and their corresponding values
    fields = {
        sport.field1: exercise.field1,
        sport.field2: exercise.field2,
        sport.field3: exercise.field3,
        sport.field4: exercise.field4,
        sport.field5: exercise.field5,
    }
    serialized_exercise['fields'] = fields

    return Response(serialized_exercise)
    

######################################## UNLOCK EXERCISE ############################################################################################
@api_view(['POST'])
def unlock_exercise(request):
    user_id = request.data.get('uID')
    exercise_id = request.data.get('eID')

    if not user_id or not exercise_id:
        return Response({"error": "Both userID and exerciseID are required parameters."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        user = Users.objects.get(uID=user_id)
    except Users.DoesNotExist:
        return Response({"error": "User not found."}, status=status.HTTP_404_NOT_FOUND)

    # Check if the user has enough points to unlock the exercise
    if user.points < 5:
        return Response({"error": "Insufficient points to unlock the exercise."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        # Deduct 5 points from the user's points
        user.points -= 5
        user.save()

        # Add entry to the Unlocked table to indicate that the exercise is unlocked for the user
        Unlocked.objects.create(uID=user, eID=Exercises.objects.get(eID=exercise_id))

        return Response({"message": "Exercise unlocked successfully."}, status=status.HTTP_200_OK)

    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


########################################### PRODUCE/SAVE WORKOUT STATS ###############################################################################
# View to return exercise statistics
@api_view(['GET'])
def get_exercise_stats(request):
    uID = request.query_params.get('uID')
    eID = request.query_params.get('eID')

    if not uID or not eID:
        return Response({"error": "uID and eID are required parameters."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        stats = Aggregated.objects.get(uID=uID, eID=eID)

        # Extract relevant data from the stats object, avoiding direct inclusion of model instances
        response_data = {
            'uID': stats.uID_id,
            'eID': stats.eID_id,
            'count': stats.CoT,
            'avgTOC': round(stats.avgTOC, 1),
            'avgChallenging': round(stats.avgChallenging, 1),
            'avgFeedback': round(stats.avgFeedback, 1),
            # ... other aggregated data as needed ...
        }

        return Response(response_data, status=status.HTTP_200_OK)
    except Aggregated.DoesNotExist:
        return Response({"error": "Statistics not found."}, status=status.HTTP_404_NOT_FOUND)
    

def update_aggregated_stats(uID, eID):
    with transaction.atomic():
        instance_stats = Analytical.objects.filter(uID=uID, eID=eID)
        count = instance_stats.count()

        if count > 0:
            avg_toc = instance_stats.aggregate(Avg('toc'))['toc__avg']
            avg_challenging = instance_stats.aggregate(Avg('challenging'))['challenging__avg']
            avg_feedback = instance_stats.aggregate(Avg('feedback'))['feedback__avg']

            try:
                aggregated_stats = Aggregated.objects.get(uID=uID, eID=eID)
                aggregated_stats.count = count
                aggregated_stats.avgTOC = round(avg_toc, 1)
                aggregated_stats.avgChallenging = round(avg_challenging, 1)
                aggregated_stats.avgFeedback = round(avg_feedback, 1)
                # ... update other aggregated fields if needed ...
                aggregated_stats.save()
            except Aggregated.DoesNotExist:
                # Create new aggregated record if it doesn't exist
                Aggregated.objects.create(
                    uID=uID, eID=eID, count=count,
                    avgTOC=round(avg_toc, 1), 
                    avgChallenging=round(avg_challenging, 1),
                    avgFeedback=round(avg_feedback, 1),
                    # ... other aggregated fields if needed ...
                )


@api_view(['POST'])
def save_exercise_instance_stats(request):
    data = request.data
    data['timestamp'] = timezone.now().strftime('%Y-%m-%dT%H:%M:%SZ')

    try:
        # Validate data (ensure all required fields are present)
        serializer = AnalyticalSerializer(data=data)  
        if serializer.is_valid():
            # Save to AnalyticalTable
            instance_stats = serializer.save() # the timestamp is in data now
            # Call the aggregation update function
            update_aggregated_stats(instance_stats.uID, instance_stats.eID) 

            return Response({"message": "Statistics saved successfully."}, status=status.HTTP_201_CREATED)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)



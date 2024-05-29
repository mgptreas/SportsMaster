from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .models import Users, UserAuth
from .serializers import UserSerializer, UserAuthSerializer


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
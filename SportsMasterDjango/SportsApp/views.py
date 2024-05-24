from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .models import Users, UserAuth
from .serializers import UserSerializer, UserAuthSerializer
import json

@api_view(['POST'])
def register_user(request):
    if request.method == 'POST':
        data = request.data
        user_serializer = UserSerializer(data=data)
        
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
                print(user_auth_serializer.errors)  # Print errors to console
                return Response(user_auth_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        
        print(user_serializer.errors)  # Print errors to console
        return Response(user_serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    

@api_view(['GET'])
def check_user_exists(request):
    username = request.query_params.get('username')
    email = request.query_params.get('email')
    print(f"Received username: {username}")  # Debugging statement

    if username:
        user_exists = Users.objects.filter(username=username).exists()
        if user_exists:
            return Response({"message": "Username already exists."}, status=status.HTTP_200_OK)
        else:
            return Response({"message": "Username is available."}, status=status.HTTP_200_OK)

    if email:
        user_exists = Users.objects.filter(email=email).exists()
        if user_exists:
            return Response({"message": "Email already exists."}, status=status.HTTP_200_OK)
        else:
            return Response({"message": "Email is available."}, status=status.HTTP_200_OK)

    return Response({"message": "Please provide a username or email to check."}, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def get_all_users(request):
    users = Users.objects.all()
    users_data = [{"username": user.username, "email": user.email} for user in users]
    return Response(users_data)
from rest_framework import serializers
from .models import Users, UserAuth, Unlocked, Sports, Exercises

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = Users
        fields = '__all__'

class UserAuthSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserAuth
        fields = '__all__'
    
class UnlockedSerializer(serializers.ModelSerializer):
    user = serializers.CharField(source='uID.username')
    exercise = serializers.CharField(source='eID.description')

    class Meta:
        model = Unlocked
        fields = ['user', 'exercise']


class SportsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sports
        fields = ['sID', 'name', 'field1', 'field2', 'field3', 'field4', 'field5']


class ExercisesSerializer(serializers.ModelSerializer):
    class Meta:
        model = Exercises
        fields = ['eID','sID', 'name', 'description', 'TOC', 'video', 'difficulty', 'field1', 'field2', 'field3', 'field4', 'field5'] 
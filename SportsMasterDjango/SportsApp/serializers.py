from rest_framework import serializers
from .models import Users, UserAuth

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = Users
        fields = '__all__'

class UserAuthSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserAuth
        fields = '__all__'
from django.urls import path
from . import views

urlpatterns = [
    path('register/', views.register_user, name='register_user'),
    path('check_user/', views.check_user_exists, name='check_user_exists'),
    path('get_all_users/', views.get_all_users, name='get_all_users'),
]
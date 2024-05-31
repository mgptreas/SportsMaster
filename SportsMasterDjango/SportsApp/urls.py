from django.urls import path
from . import views

urlpatterns = [
    path('register_user/', views.register_user, name='register_user'),
    path('login_user/', views.login_user, name='login_user'),
    path('get_all_users/', views.get_all_users, name='get_all_users'),
    path('get_user_profile/', views.get_user_profile, name='get_user_profile'),
    path('select_workout/', views.select_workout, name='select_workout'),
    path('check_unlocked/', views.check_unlocked, name='check_unlocked'),
]
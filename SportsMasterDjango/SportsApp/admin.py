from django.contrib import admin
from .models import Users, UserAuth, Sports, Exercises, Unlocked, Analytical, Aggregated

# Register your models here
admin.site.register(Users)
admin.site.register(UserAuth)
admin.site.register(Sports)
admin.site.register(Exercises)
admin.site.register(Unlocked)
admin.site.register(Analytical)
admin.site.register(Aggregated)

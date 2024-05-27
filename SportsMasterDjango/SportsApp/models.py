from django.db import models

class Users(models.Model):
    username = models.CharField(max_length=255)
    name = models.CharField(max_length=255, blank=True, null=True)
    email = models.EmailField(blank=True, null=True)
    height = models.FloatField(blank=True, null=True)
    weight = models.FloatField(blank=True, null=True)
    points = models.IntegerField(blank=True, null=True)
    premium = models.BooleanField(default=False)
    class Meta:
        db_table = 'Users'  # Specify the table name explicitly

class UserAuth(models.Model):
    user = models.OneToOneField(Users, on_delete=models.CASCADE, primary_key=True)
    username = models.CharField(max_length=255)
    password = models.CharField(max_length=255)
    class Meta:
        db_table = 'UserAuth'  # Specify the table name explicitly

class Sports(models.Model):
    name = models.CharField(max_length=255)
    field1 = models.CharField(max_length=255, blank=True, null=True)
    field2 = models.CharField(max_length=255, blank=True, null=True)
    field3 = models.CharField(max_length=255, blank=True, null=True)
    field4 = models.CharField(max_length=255, blank=True, null=True)
    field5 = models.CharField(max_length=255, blank=True, null=True)
    class Meta:
        db_table = 'Sports'  # Specify the table name explicitly

class Exercises(models.Model):
    sport = models.ForeignKey(Sports, on_delete=models.CASCADE)
    description = models.TextField()
    video = models.CharField(max_length=255, blank=True, null=True)
    difficulty = models.IntegerField()
    field1 = models.IntegerField(blank=True, null=True)
    field2 = models.IntegerField(blank=True, null=True)
    field3 = models.IntegerField(blank=True, null=True)
    field4 = models.IntegerField(blank=True, null=True)
    field5 = models.IntegerField(blank=True, null=True)
    class Meta:
        db_table = 'Exercises'  # Specify the table name explicitly

class Unlocked(models.Model):
    user = models.ForeignKey(Users, on_delete=models.CASCADE)
    exercise = models.ForeignKey(Exercises, on_delete=models.CASCADE)

    class Meta:
        db_table = 'Unlocked'  # Specify the table name explicitly
        unique_together = (('user', 'exercise'),)

class Analytical(models.Model):
    user = models.ForeignKey(Users, on_delete=models.CASCADE)
    exercise = models.ForeignKey(Exercises, on_delete=models.CASCADE)
    timestamp = models.CharField(max_length=255)
    toc = models.IntegerField()
    challenging = models.IntegerField()
    feedback = models.IntegerField()
    class Meta:
        db_table = 'Analytical'  # Specify the table name explicitly

class Aggregated(models.Model):
    user = models.ForeignKey(Users, on_delete=models.CASCADE)
    exercise = models.ForeignKey(Exercises, on_delete=models.CASCADE)
    avgTOC = models.FloatField()
    avgChallenging = models.FloatField()
    avgFeedback = models.FloatField()
    commonness = models.IntegerField()
    rarity = models.IntegerField()
    CoT = models.IntegerField()

    class Meta:
        db_table = 'Aggregated'  # Specify the table name explicitly
        unique_together = (('user', 'exercise'),)
 
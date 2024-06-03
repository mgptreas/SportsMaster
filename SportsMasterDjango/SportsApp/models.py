from django.db import models

class Users(models.Model):
    uID = models.AutoField(primary_key=True)  # Use AutoField to match SERIAL type in SQL
    username = models.CharField(max_length=255, unique=True)  # Ensure username is unique if needed
    name = models.CharField(max_length=255, blank=True, null=True)
    email = models.EmailField(blank=True, null=True)
    height = models.FloatField(blank=True, null=True)
    weight = models.FloatField(blank=True, null=True)
    points = models.IntegerField(blank=True, null=True)
    premium = models.BooleanField(default=False)

    class Meta:
        db_table = 'Users'  # Specify the table name explicitly

class UserAuth(models.Model):
    uID = models.OneToOneField(Users, on_delete=models.CASCADE, primary_key=True)  # Use the same primary key as Users
    username = models.CharField(max_length=255)
    password = models.CharField(max_length=255)

    class Meta:
        db_table = 'UserAuth'  # Specify the table name explicitly

class Sports(models.Model):
    sID = models.AutoField(primary_key=True)  # Use AutoField to match SERIAL type in SQL
    name = models.CharField(max_length=255)
    field1 = models.CharField(max_length=255, blank=True, null=True)
    field2 = models.CharField(max_length=255, blank=True, null=True)
    field3 = models.CharField(max_length=255, blank=True, null=True)
    field4 = models.CharField(max_length=255, blank=True, null=True)
    field5 = models.CharField(max_length=255, blank=True, null=True)

    class Meta:
        db_table = 'Sports'  # Specify the table name explicitly

class Exercises(models.Model):
    eID = models.AutoField(primary_key=True)  # Use AutoField to match SERIAL type in SQL
    sID = models.ForeignKey(Sports, on_delete=models.CASCADE, db_column='sID')  # Use db_column to specify foreign key column name
    name = models.TextField()
    description = models.TextField()
    TOC = models.IntegerField(blank=True, null=True)
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
    uID = models.ForeignKey(Users, on_delete=models.CASCADE, db_column='uID')  # Use db_column to specify foreign key column name
    eID = models.ForeignKey(Exercises, on_delete=models.CASCADE, db_column='eID')  # Use db_column to specify foreign key column name

    class Meta:
        db_table = 'Unlocked'  # Specify the table name explicitly
        unique_together = (('uID', 'eID'),)  # Define composite primary key

class Analytical(models.Model):
    instanceID = models.AutoField(primary_key=True)  # Use AutoField to match SERIAL type in SQL
    uID = models.ForeignKey(Users, on_delete=models.CASCADE, db_column='uID')  # Use db_column to specify foreign key column name
    eID = models.ForeignKey(Exercises, on_delete=models.CASCADE, db_column='eID')  # Use db_column to specify foreign key column name
    timestamp = models.CharField(max_length=255)
    toc = models.IntegerField()
    challenging = models.IntegerField()
    feedback = models.IntegerField()

    class Meta:
        db_table = 'Analytical'  # Specify the table name explicitly

class Aggregated(models.Model):
    uID = models.ForeignKey(Users, on_delete=models.CASCADE, db_column='uID')  # Use db_column to specify foreign key column name
    eID = models.ForeignKey(Exercises, on_delete=models.CASCADE, db_column='eID')  # Use db_column to specify foreign key column name
    avgTOC = models.FloatField()
    avgChallenging = models.FloatField()
    avgFeedback = models.FloatField()
    commonness = models.IntegerField()
    rarity = models.IntegerField()
    CoT = models.IntegerField()

    class Meta:
        db_table = 'Aggregated'  # Specify the table name explicitly
        unique_together = (('uID', 'eID'),)  # Define composite primary key

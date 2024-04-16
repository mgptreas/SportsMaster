#NEEDS FIXING, HAVEN'T EXECUTED YET



import psycopg2
from faker import Faker
import random

# Connect to the PostgreSQL database
conn = psycopg2.connect(
    dbname="your_database_name",
    user="your_username",
    password="your_password",
    host="your_host",
    port="your_port"
)

# Create a cursor object
cur = conn.cursor()

# Initialize Faker to generate dummy data
fake = Faker()

# Function to generate dummy data for Users table
def generate_users_data(num_records):
    users_data = []
    for _ in range(num_records):
        username = fake.user_name()
        name = fake.name()
        email = fake.email()
        height = random.uniform(150, 200)  # Random height between 150 and 200 cm
        weight = random.uniform(50, 100)   # Random weight between 50 and 100 kg
        points = random.randint(0, 1000)   # Random points
        users_data.append((username, name, email, height, weight, points))
    return users_data

# Function to generate dummy data for Unlocked table
def generate_unlocked_data(num_records, num_users, num_exercises):
    unlocked_data = []
    for _ in range(num_records):
        uID = random.randint(1, num_users)
        eID = random.randint(1, num_exercises)
        unlocked_data.append((uID, eID))
    return unlocked_data

# Function to generate dummy data for UserAuth table
def generate_userauth_data(users_data):
    userauth_data = [(uID, data[0], "password123") for uID, data in enumerate(users_data, start=1)]
    return userauth_data

# Function to generate dummy data for Exercises table
def generate_exercises_data(num_records, num_sports):
    exercises_data = []
    for _ in range(num_records):
        sID = random.randint(1, num_sports)
        description = fake.text()
        video_url = fake.url()
        difficulty = random.choice(['easy', 'medium', 'hard'])
        exercises_data.append((sID, description, video_url, difficulty))
    return exercises_data

# Function to generate dummy data for Sports table
def generate_sports_data(num_records):
    sports_data = []
    for _ in range(num_records):
        name = fake.word()
        sports_data.append((name,))
    return sports_data

# Generate dummy data
num_users = 100
num_exercises = 200
num_sports = 10

users_data = generate_users_data(num_users)
unlocked_data = generate_unlocked_data(num_users * 10, num_users, num_exercises)
userauth_data = generate_userauth_data(users_data)
exercises_data = generate_exercises_data(num_exercises, num_sports)
sports_data = generate_sports_data(num_sports)

# Insert dummy data into the database
cur.executemany("""
    INSERT INTO Users (username, name, email, Height, Weight, Points)
    VALUES (%s, %s, %s, %s, %s, %s)
""", users_data)

cur.executemany("""
    INSERT INTO Unlocked (uID, eID)
    VALUES (%s, %s)
""", unlocked_data)

cur.executemany("""
    INSERT INTO UserAuth (uID, username, password)
    VALUES (%s, %s, %s)
""", userauth_data)

cur.executemany("""
    INSERT INTO Exercises (sID, description, video_url, difficulty)
    VALUES (%s, %s, %s, %s)
""", exercises_data)

cur.executemany("""
    INSERT INTO Sports (name)
    VALUES (%s)
""", sports_data)

# Commit the transaction
conn.commit()

# Close the cursor and connection
cur.close()
conn.close()

print("Dummy data insertion complete.")

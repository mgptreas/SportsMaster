import psycopg2
import random
from datetime import datetime



# Connect to your PostgreSQL database through SSH tunnel
conn = psycopg2.connect(
    dbname="SportsMasterDB",
    user="SMDBadmin",
    password="!Qo82oo2",
    host="localhost",
    port=5432
)

# Create a cursor object
cur = conn.cursor()

# Dummy data for Users
users_data = [
    ("user1", "User One", "user1@example.com", 175, 70, 100, False),
    ("user2", "User Two", "user2@example.com", 180, 75, 150, True),
    ("user3", "User Three", "user3@example.com", 170, 65, 200, False)
]

# Insert Users data into Users table
for user in users_data:
    cur.execute('INSERT INTO USERS (username, name, email, Height, Weight, Points, premium) VALUES (%s, %s, %s, %s, %s, %s, %s)', user)

# Dummy data for Sports
sports_data = [
    ("Sport A", "Field A1", "Field A2", "Field A3", "Field A4", "Field A5"),
    ("Sport B", "Field B1", "Field B2", "Field B3", "Field B4", "Field B5"),
    ("Sport C", "Field C1", "Field C2", "Field C3", "Field C4", "Field C5")
]

# Insert Sports data into Sports table
for sport in sports_data:
    cur.execute("INSERT INTO Sports (name, field1, field2, field3, field4, field5) VALUES (%s, %s, %s, %s, %s, %s)", sport)

# Dummy data for Exercises
exercises_data = []
for i in range(50):  # Assuming you want 50 exercises
    exercise = (
        random.randint(1, 3),  # Randomly choose a sport ID
        f"Exercise {i+1} description",
        f"https://example.com/exercise_{i+1}.mp4",
        random.randint(1, 5),  # Random difficulty level
        random.randint(0, 10),  # Field1
        random.randint(0, 10),  # Field2
        random.randint(0, 10),  # Field3
        random.randint(0, 10),  # Field4
        random.randint(0, 10)   # Field5
    )
    exercises_data.append(exercise)

# Insert Exercises data into Exercises table
for exercise in exercises_data:
    cur.execute("INSERT INTO Exercises (sID, description, video, difficulty, field1, field2, field3, field4, field5) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", exercise)

# Dummy data for Unlocked (assuming each user unlocked all exercises)
for user_id in range(1, 4):  # Assuming you have 3 users
    for exercise_id in range(1, 51):  # Assuming you have 50 exercises
        unlocked_data = (user_id, exercise_id)
        cur.execute("INSERT INTO Unlocked (uID, eID) VALUES (%s, %s)", unlocked_data)

# Dummy data for Analytical
for user_id in range(1, 4):  # Assuming you have 3 users
    for exercise_id in range(1, 51):  # Assuming you have 50 exercises
        for _ in range(random.randint(1, 5)):  # Random number of instances per exercise
            timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            analytical_data = (
                user_id,
                exercise_id,
                timestamp,
                random.randint(0, 10),  # TOC
                random.randint(1, 10),  # Challenging
                random.randint(1, 10)   # Feedback
            )
            cur.execute("INSERT INTO Analytical (uID, eID, timestamp, toc, challenging, feedback) VALUES (%s, %s, %s, %s, %s, %s)", analytical_data)

# Commit changes
conn.commit()


# Close cursor and connection to PostgreSQL database
cur.close()
conn.close()

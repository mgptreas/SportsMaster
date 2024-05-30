import psycopg2
import random

# Connect to your PostgreSQL database
conn = psycopg2.connect(
    dbname="SportsMasterDB",
    user="SMDBadmin",
    password="!Qo82oo2",
    host="localhost",
    port=5432
)

# Create a cursor object
cur = conn.cursor()

# Helper function to generate a random value in steps of 10 from 0 to 50
def get_random_value():
    return random.choice([0, 10, 20, 30, 40, 50])

# Fetch all unique user-exercise pairs from the Analytical table
cur.execute('''
    SELECT "uID", "eID"
    FROM "Analytical"
    GROUP BY "uID", "eID";
''')
user_exercise_pairs = cur.fetchall()

# Process each user-exercise pair
for user_id, exercise_id in user_exercise_pairs:
    # Calculate the aggregate values for each user-exercise pair
    cur.execute('''
        SELECT
            AVG(toc) AS avg_toc,
            AVG(challenging) AS avg_challenging,
            AVG(feedback) AS avg_feedback,
            COUNT(*) AS count_of_times
        FROM "Analytical"
        WHERE "uID" = %s AND "eID" = %s;
    ''', (user_id, exercise_id))
    result = cur.fetchone()
    
    avg_toc = result[0]
    avg_challenging = result[1]
    avg_feedback = result[2]
    count_of_times = result[3]
    
    # Generate random values for commonness and rarity
    commonness = get_random_value()
    rarity = get_random_value()
    
    # Insert the aggregated data into the Aggregated table
    cur.execute('''
        INSERT INTO "Aggregated" ("uID", "eID", "avgTOC", "avgChallenging","avgFeedback", "commonness", "rarity", "CoT")
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s);
    ''', (user_id, exercise_id, avg_toc, avg_challenging, avg_feedback, commonness, rarity, count_of_times))

# Commit changes
conn.commit()

# Close cursor and connection to PostgreSQL database
cur.close()
conn.close()

print("Aggregated table has been populated successfully.")

import psycopg2

# Connect to your PostgreSQL database through SSH tunnel
conn = psycopg2.connect(
    dbname="SportsMasterDB",
    user="SMDBadmin",
    password="Qo82oo2",
    host="localhost",
    port=5432
)

conn.close() 
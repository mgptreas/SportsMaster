Το backend το οποίο χρησιμοποιεί το django framework βρίσκεται στον φάκελο SMDjango. Στον υποφάκελο SMDjangoApp υπάρχουν τα python αρχεία, με τις μεθόδους να βρίσκονται στο views.py
Το frontend (Android Studio) βρίσκεται στον φάκελο SportsMasterApp. Το path για τις κλάσεις και τα activitys είναι το ακόλουθο: ".\SportsMaster\SportsMasterApp\app\src\main\java\com\example\sportsmasterapp" (Windows)
Τα xml αρχεία και τα assets βρίσκονται στο: ".\SportsMaster\SportsMasterApp\app\src\main\res" (Windows path)
Στο developement χρησιμοποιήσαμε Android Studio Flamingo και σαν device το Pixel 6 API 33, με minSDK = 26
Για να τρέξετε το backend χρειάζεται να κάνετε ssh στο EC2: ssh -i "path-to/SMKey.pem" -L 5432:sportsmasterdb.cf6qwyuwgxvx.eu-central-1.rds.amazonaws.com:5432 Administrator@3.71.21.250
Τέλος σε ένα άλλο cmd το οποίο θα το έχετε ανοίξει μέσα στον αρχικό φάκελο του django (SMDjango) θα πρέπει να ξεκινήσετε τον django server με την εντολή: "python manage.py runserver"
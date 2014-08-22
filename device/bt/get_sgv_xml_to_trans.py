import MySQLdb

db = MySQLdb.connect(host="localhost", user="root", passwd="raspberry", db="cloop")

cur = db.cursor()
cur.execute("SELECT sgv_id, device_id, datetime_recorded, sgv, transfered from sgvs where transfered='no'")

for row in cur.fetchall():
  

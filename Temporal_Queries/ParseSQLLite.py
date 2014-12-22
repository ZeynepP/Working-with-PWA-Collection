import sys

sys.path.append('/home/pehlivanz/GoogleTrends/Python/')

import numpy as np
import matplotlib.pyplot as plt
import sqlite3
import re
import urllib



conn = sqlite3.connect('/home/pehlivanz/9')
c = conn.cursor()


c.execute('''CREATE TABLE Queries
             (id, query)''')
c.execute('''CREATE TABLE Queries_Values
             (id, q_id,  week, trends)''')
#getting only google trends with data content like '%Data table%'
c.execute("SELECT orig_url, content FROM document_contents INNER JOIN documents ON documents.id=document_contents.id WHERE content like '%Data table%'")
iddoc_all = c.fetchall()
counter = 0
print len(iddoc_all)
for a in iddoc_all:
    the_filename = a[0].split("q=")[1].replace("\\","")
    content = a[1]
    a = re.findall("\"f(.+?),\"",content,flags=0)
    if(len(a) == 104):
        counter = counter+1
print(counter)
counter = 0
internalcounter = 0
for a in iddoc_all:
    counter = counter + 1
    the_filename = urllib.unquote(a[0].split("q=")[1].replace("\\",""))
    content = a[1]
    statement = "INSERT INTO Queries VALUES (%s,%s);" %(str(counter),"'"+the_filename + "'")
    print(statement)
    c.execute(statement)
    internalcounter = 0
    for (letters) in re.findall("\"f(.+?),\"",content,flags=0):
        internalcounter = internalcounter +1
        letters =  letters.replace("\\u2013","-")
        letters = letters.replace("\"},{\"v\"","")
        letters = letters.replace("\":\"","")
        letters = letters.replace("\'","")
        letters = letters.replace("u","")
        date  = letters.split(":")[0]
        trend =  letters.split(":")[1]
        strcommand = "INSERT INTO Queries_Values  VALUES (%s,%s,%s,%s);" % ( str(internalcounter ), str(counter ), "'"+ date + "'" , trend )
        print(strcommand)
        c.execute(strcommand)
     
     
conn.commit()
conn.close()

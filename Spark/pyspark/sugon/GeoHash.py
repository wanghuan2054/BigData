import time
from datetime import datetime

import geohash

def geoHash(jd , wd ,precision=10 ) :
    try :
        jd1 = float(jd)
        wd1 = float(wd)
        data = geohash.encode(jd1, wd1, precision)
        return data
    except :
        return None

if __name__ == '__main__' :

    data = geoHash('12.34', '123.325')
    print(data)
    data = geoHash('123#','12.34')
    print(data)
import Geohash as geohash
import pyspark

def geoHash(jd , wd ,precision=10 ) :
    try :
        jd1 = float(jd)
        wd1 = float(wd)
    except :
        return ''
    print(jd1)
    print(wd1)
    try :
       data = geohash.encode(jd1, wd1, precision)
       return data
    except :
        return ''

if __name__ == '__main__' :
    data = geoHash('12.34', '112.325')
    print(data)
    data = geoHash('123#','12.34')
    print(data)
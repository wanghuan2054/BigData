import geohash

def geoHash(jd , wd ,precision=10 ) :
    if not jd.isdigit():
        jd1 = float(jd)
    if not wd.isdigit():
        wd1 = float(wd)
    data = geohash.encode(jd1, wd1, precision)
    return data

if __name__ == '__main__' :
    data = geoHash('116.255421', '40.201209')
    print(data)
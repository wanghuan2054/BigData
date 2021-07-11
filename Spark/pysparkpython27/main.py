# coding=utf-8
# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import datetime
import time


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print("Hi, {0}".format(name))  # Press Ctrl+F8 to toggle the breakpoint.

def getValue(x) :
    if x == 1  :
     return 1
    elif x==2 :
     return 2
    else:
     return "Error"

def get_day_nday_ago(date,n):
    DATE_FORMAT = '%Y%m%d'
    SUFFIX_TIME = '000000'
    t = time.strptime(date, DATE_FORMAT)
    y, m, d = t[0:3]
    date = (datetime.datetime(y, m, d) - datetime.timedelta(n))
    return date.strftime(DATE_FORMAT)+SUFFIX_TIME

if __name__ == '__main__':
    DATE_FORMAT = '%Y%m%d%H%M%S'
    # a = get_day_nday_ago(datetime.date.today().strftime(DATE_FORMAT), 0)
    # print a

    print datetime.datetime.now().strftime(DATE_FORMAT)
    liststr = ['a','b']
    print '-'.join(liststr)
#     start_time = datetime.datetime.now()
#     time.sleep(5)
#     end_time = datetime.datetime.now()
#     delta = end_time - start_time
#     duration_str = time.strftime("%H:%M:%S", time.gmtime(delta.total_seconds())
# )
#
#     print "start time:", start_time
#     print "end time:", end_time
#     print "duration:", duration_str


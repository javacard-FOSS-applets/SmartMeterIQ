
reset

set grid
unset mouse
set terminal x11 persist
#set terminal postscript color
#pngset terminal png

set key right bottom
set xlabel "RSA key size in bits (exponent size according to Lenstra)"
set xrange [ 500 : 800 ]
set xtics 512,32
set yrange [0 : ]
set ylabel "time in sec"

#f1(x) = a1*x**3 + b1*x**1 + c1*x + d1
#f2(x) = a2*x**3 + b2*x**2 + c2*x + d2
#FIT_LIMIT = 1e-6

#fit f1(x) 'vector-exp-base-5-fac-5-h5-wired.data' using 2:5 via a1, b1, c1, d1
#fit f2(x) 'vector-exp-base-5-fac-5-h3-wireless.data' \
#          using 2:5 via a2, b2, c2, d2

set title "Basic OV Chip protocols, RSA variant (4 bases, RSA exponent, Montgomery multiplication)"

set pointsize 1.5
#pngset pointsize 1

plot \
     "test-size-h5-wired.resign-data" using 2:5 t 'resigning 4.1 wired', \
     "test-size-h5-wired.gate-data" using 2:5 t 'prove 4.1 wired', \
     "test-size-h5-wireless.resign-data" using 2:5 t 'resigning 4.1 wireless', \
     "test-size-h5-wireless.gate-data" using 2:5 t 'prove 4.1 wireless', \
     "test-size-blue-wireless.resign-data" using 2:5 t \
                                             'resigning blue wireless', \
     "test-size-blue-wireless.gate-data" using 2:5 t 'prove blue wireless'


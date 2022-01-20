import wiringpi as wp
import time
import sys

#Waits for pin to go to a specified level or until timeout is reached.
def waitPinLevel(pin, level, timeout):
	done = False
	start = time.time_ns() / 1000
	
	while not done:
		now = time.time_ns() / 1000
		micros = now - start
		if wp.digitalRead(pin) == level or micros > timeout:
			done = True
	return micros

#Waits a determined amount of microseconds.
def delayMicros(micros):
	start = time.time_ns()
	elapsed = 0
	while elapsed < ((micros - 1) * 1000):
		elapsed = time.time_ns() - start
	return elapsed

#wiringPi numbering
pin_echo = 2
pin_trigger = 0

#Setup
wp.wiringPiSetup()
wp.pinMode(pin_echo, wp.INPUT)
wp.pinMode(pin_trigger, wp.OUTPUT)
wp.digitalWrite(pin_trigger, wp.LOW)
time.sleep(0.5)

while True:
	#Sensor activation
	wp.digitalWrite(pin_trigger, wp.HIGH)
	elapsed = delayMicros(10)	
	wp.digitalWrite(pin_trigger, wp.LOW)
	
	#Sensor reading
	waitPinLevel(pin_echo, wp.HIGH, 5000)
	if wp.digitalRead(pin_echo) == wp.HIGH:
		#Waiting for the signal to return
		elapsed = waitPinLevel(pin_echo, wp.LOW, 60000)
		if wp.digitalRead(pin_echo) == wp.LOW:
			
			#Computing distance
			distance = 0.034 * elapsed / 2.0
			if elapsed > 38000:
				print("Out of range")
			else:
				print("" + str(distance) + "")
		else:
			print("Timeout")
	
	sys.stdout.flush()
	time.sleep(1)

import sys # Para leer los argumentos pasados en las lineas de comando
import time # Para calcular el tiempo de ejecución
import glob # Para leer varios ficheros de un directorio
from Funciones import *

def main():
	arguments_counter = len(sys.argv) - 1
	if arguments_counter == 0:
		print("\nYou must specify an argument. If help is needed, type -h or --help when executing.")
	elif arguments_counter == 1 and (sys.argv[1] == "-h" or sys.argv[1] == "--help"):
		help()
	elif sys.argv[1] == "-d" or sys.argv[1] == "--directory":
		proccess_directory()
	elif sys.argv[1] == "-f" or sys.argv[1] == "--file":
			proccess_file()
	else:
		exit()

def proccess_directory():
	for file in glob.glob(sys.argv[2] + "/*"):
		print("\nAnalizando el fichero " + file)
		length, keyPad = readData(file)
		solve(length, keyPad)

def proccess_file():
	file_address = sys.argv[2]
	length, keyPad = readData(file_address)
	solve(length, keyPad)

def solve(length, keyPad):
	if sys.argv[3] == "-st" or sys.argv[3] == "--tabulation":
		start_time = time.time()
		totalCount, listRes = tabulation(length, keyPad)
		elapsed_time = time.time() - start_time
	elif sys.argv[3] == "-sm" or sys.argv[3] == "--memoization":
		start_time = time.time()
		totalCount, listRes = memoization(length, keyPad)
		elapsed_time = time.time() - start_time
	elif sys.argv[3] == "-check":
		totalCountT, listResT = tabulation(length, keyPad)
		totalCountM, listResM = memoization(length, keyPad)
		if check(totalCountT, listResT, totalCountM, listResM):
			print("Tabulation and Memoization both obtain the same results!")
		else:
			print("There is something incorrect. Tabulation and Memoization do not obtain the same results.")
	else: 
		exit()
		
	if sys.argv[3] != "-check":
		output(totalCount, listRes, length, elapsed_time)

def output(totalCount, listRes, length, elapsed_time):
	for argument in range(4, len(sys.argv)):
		if sys.argv[argument] == "-nc" or sys.argv[argument] == "--numberCombinations":
			print("The total amount of possible combinations is:  " + str(totalCount)) 
		elif sys.argv[argument] == "-t" or sys.argv[argument] == "--time":
			print("The elapsed time is:                           " + str(elapsed_time) + " seconds")
		elif sys.argv[argument] == "-nd" or sys.argv[argument] == "--numberDigits":
			print("The number of digits per combination is:       " + str(length))
		elif sys.argv[argument] == "-dc" or sys.argv[argument] == "--displayCombinations":
			print("The possible combinations are:")
			if totalCount == 0:
				print("There are no keys to be pressed (Count: 0)")
			else:
				num = 0
				while num < 10:
					count = 0
					strDC = "If we start with " + str(num) + ", valid numbers will be: "
					for i in range(0, len(listRes)):
						combination = listRes[i]
						if combination[0] == str(num):
							strDC += combination + " "
							count += 1
					strDC += "(Count: " + str(count) + ")."
					print(strDC)
					num += 1

if __name__ == '__main__':
	main()
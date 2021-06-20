def help():
	print("\nOptional arguments:")
	print("-----------------------------------------------------------------------------------------")
	print("Short argument          Long argument              Explanation")
	print("-----------------------------------------------------------------------------------------")
	print("-h                      --help                     Shows this message and exit.")
	print("\n-d [DIRECTORY]          --directory [DIRECTORY]    directory (proccess many files).")
	print("-f [FILE]               --file [FILE]              file (proccess a single file).")
	print("\n-sm                     --memoization              Solve it with Memoization.")
	print("-st                     --tabulation               Solve it with Tabulation.")
	print("-check                                             Check whether Memoization and Tabulation got the same results.")
	print("\n-t                      --time                     Display time.")
	print("-nd                     --numberDigits             Display the number of digits (length).")
	print("-nc                     --numberCombinations       Display the number of possible combinations.")	
	print("-dc                     --displayCombinations      Display the possible combinations.")
	
	exit()

def readData(file_address):
	length = 0
	keyPad = [[0 for j in range(5)] for i in range(6)] # Matriz de 6 filas x 5 columnas
	count = 0
	file = open(file_address, "r")
	for line in file:
		parts = line.split()
		if count > 0:
			keyPad[count-1][0] = parts[0]
			keyPad[count-1][1] = parts[1]
			keyPad[count-1][2] = parts[2]
			keyPad[count-1][3] = parts[3]
			keyPad[count-1][4] = parts[4]
		else:
			length = int(parts[2])
		count += 1
	file.close()
	return length, keyPad

def memoization(length, keyPad):
	def press_key(i, j, length):
		if (i, j, length) not in mem:
			if (length == 0) or (i<1) or (i>4) or (j<1) or (j>3) or (keyPad[i][j] == '*') or (keyPad[i][j] == '#'): 
				# No poder presionar ninguna tecla, salirse fuera del teclado o presionar teclas inválidas.
				mem[i, j, length] = [0, ""]
			else:
				if length == 1: 
					# Caso base.
					mem[i, j, length] = [1, keyPad[i][j]]
				else:
					# Caso general.
					pressSameKey = press_key(i, j, length-1)
					pressUpKey = press_key(i-1, j, length-1)
					pressDownKey = press_key(i+1, j, length-1)
					pressLeftKey = press_key(i, j-1, length-1)
					pressRightKey = press_key(i, j+1, length-1)
					res = [pressSameKey[0] + pressUpKey[0] + pressDownKey[0] + pressLeftKey[0] + pressRightKey[0]]
					for x in range(1, len(pressSameKey)):
						res.append(keyPad[i][j] + pressSameKey[x])
					if pressUpKey[1] != "":
						# En caso de que existan combinaciones válidas presionando la tecla de arriba, 
						# éstas se añaden a una lista añadiendo el valor de la tecla origen al principio de todas las combinaciones mencionadas:
						for x in range(1, len(pressUpKey)):
							res.append(keyPad[i][j] + pressUpKey[x])
					if pressDownKey[1] != "":
						for x in range(1, len(pressDownKey)):
							res.append(keyPad[i][j] + pressDownKey[x])
					if pressLeftKey[1] != "":
						for x in range(1, len(pressLeftKey)):
							res.append(keyPad[i][j] + pressLeftKey[x])
					if pressRightKey[1] != "":
						for x in range(1, len(pressRightKey)):
							res.append(keyPad[i][j] + pressRightKey[x])
					mem[i, j, length] = res  
		return mem[i, j, length]

	mem = {}
	totalCount = 0
	listRes = []
	for i in range(6):
		for j in range(5):
			listComb = press_key(i, j, length) 
			totalCount += listComb[0]
			for x in range(1, len(listComb)):
				if listComb[x] != "":
					# En caso de que existan combinaciones válidas, éstas se añaden a una lista:
					listRes.append(listComb[x])
	return totalCount, listRes

def tabulation(length, keyPad):
	table = [[[[0, ""] for k in range(length+1)] for j in range(5)] for i in range(6)] # Matriz de 6 filas x 5 columnas x cada valor que puede ser la variable length empezando desde cero.
	# Casos bases:
	# Para todo k = 0, los valores de la tabla ya están igualados a cero
	if length > 0:
		# Para k = 1, solo nos interesa establecer a 1 las posiciones i y j que se corresponden con las teclas numéricas.
		for i in range(1, 5):
			for j in range(1, 4):
				if keyPad[i][j] != '*' and keyPad[i][j] != '#':
					table[i][j][1] = [1, keyPad[i][j]]
		if length > 1:
			# Rellenamos la talba con los casos generales a partir de los casos bases.
			for k in range(2, length+1):
				for i in range(1, 5):
					for j in range(1, 4):
						if keyPad[i][j] != '*' and keyPad[i][j] != '#':
							listComb = [table[i][j][k-1][0] + table[i-1][j][k-1][0] + table[i+1][j][k-1][0] + table[i][j-1][k-1][0] + table[i][j+1][k-1][0]]
							if table[i][j][k-1][1] != "":
								for x in range(1, len(table[i][j][k-1])):
									listComb.append(keyPad[i][j] + table[i][j][k-1][x])
							if table[i-1][j][k-1][1] != "":
								for x in range(1, len(table[i-1][j][k-1])):
									listComb.append(keyPad[i][j] + table[i-1][j][k-1][x])
							if table[i+1][j][k-1][1] != "":
								for x in range(1, len(table[i+1][j][k-1])):
									listComb.append(keyPad[i][j] + table[i+1][j][k-1][x])
							if table[i][j-1][k-1][1] != "":
								for x in range(1, len(table[i][j-1][k-1])):
									listComb.append(keyPad[i][j] + table[i][j-1][k-1][x])
							if table[i][j+1][k-1][1] != "":
								for x in range(1, len(table[i][j+1][k-1])):
									listComb.append(keyPad[i][j] + table[i][j+1][k-1][x])
							table[i][j][k] = listComb
	
	result = 0
	listRes = []
	for i in range(6):
		for j in range(5):
			result += table[i][j][length][0]
			for x in range(1, len(table[i][j][length])):
				if table[i][j][length][x] != "":
					listRes.append(table[i][j][length][x])

	return result, listRes

def check(totalCountT, listResT, totalCountM, listResM):
	if totalCountT != totalCountM:
		return False
	else:
		if len(listResT) != len(listResM):
			return False
		else:
			if len(listResT) != totalCountT or len(listResM) != totalCountM:
				return False
			else:
				for i in range(len(listResT)):
					if (listResT[i] not in listResM):
						return False
				for i in range(len(listResM)):
					if (listResM[i] not in listResT):
						return False
	return True
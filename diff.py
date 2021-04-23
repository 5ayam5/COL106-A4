file1 = open(input(), 'r')
file2 = open(input(), 'r')
a1 = file1.read()
a2 = file2.read()
count = 0
for i in range(0, min(len(a1), len(a2))):
	if a1[i] != a2[i]:
		if count < 1:
			print(i + 1, "\t:\n", a1[i-10:i+80], "\n", a2[i-10:i+80])
		count += 1
print(len(a1), len(a2), count)
file1.close()
file2.close()

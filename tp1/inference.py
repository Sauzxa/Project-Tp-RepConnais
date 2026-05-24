import subprocess
import sys
import tempfile
from pathlib import Path

if len(sys.argv) < 2:
    print("Veuillez entrer le nom du fichier cnf en arguments.")
    quit()

input_path = Path(sys.argv[1]).resolve()

if not input_path.exists():
    print("Fichier cnf n'existe pas dant le chemain entré.")
    quit()

project_dir = Path(__file__).resolve().parent
ubcsat = project_dir / "ubcsat"

if not ubcsat.exists():
    print("Le solveur ubcsat est introuvable. Compilez-le avec: cd tp1 && make")
    quit()

with input_path.open() as file:
    lines = file.readlines()

header_index = next(
    (index for index, content in enumerate(lines) if content.strip().startswith("p cnf")),
    None,
)

if header_index is None:
    print("Le fichier CNF ne contient pas une ligne d'en-tête 'p cnf'.")
    quit()

line = lines[header_index].split()

# extracter les information du fichier cnf
nbr_litteraux = int(line[2])
print("Le nombre des litteraux utilisé est: "+str(nbr_litteraux))
print("Le nombre de clauses originaux: "+line[3])
line[3] = str(int(line[3])+1)
print("Le nombre de clauses apres le traitement: "+line[3])
s = " "
newline = s.join(line)
newline = newline+'\n'
but = 0


litteraux = {'Na': 1, 'Nb': 2, 'Nc': 3, 'Cea': 4, 'Ceb': 5, 'Cec': 6, 'Ma': 7, 'Mb': 8, 'Mc': 9, 'Coa': 10, 'Cob': 11, 'Coc': 12}
print("Les litteraux sont:", litteraux)
print("\n")

while True:
        litteral = input("Donner le nom de litteral que vous voulez tester: ")
        
        if litteral in litteraux.keys():
             print("litteral de test valide")
             but = litteraux[litteral]
             break
        print("Litteral n'existe pas")

non_but = -1 * but
lines[header_index] = newline

# ajouter le non de litteral a la fin dans une copie temporaire
with tempfile.NamedTemporaryFile("w", suffix=".cnf", dir=project_dir, delete=False) as temp_cnf:
    temp_cnf.writelines(lines)
    temp_cnf.write('\n'+str(non_but)+' 0')
    temp_cnf_path = temp_cnf.name

try:
    result = subprocess.run(
        [str(ubcsat), "-alg", "saps", "-i", temp_cnf_path, "-solve"],
        check=False,
        capture_output=True,
        text=True,
    )
    check = "# No Solution found for"
    infere = False
    for i in result.stdout.splitlines():
        if check in i:
            infere = True

    if infere == True:
        print("BC infère '" + litteral + "'")
    else:
        print("BC n'infère pas '" + litteral + "'")
finally:
    Path(temp_cnf_path).unlink(missing_ok=True)
    


$param1 = $args[0] # Nume fisier java
#Write-Host $param1

$param2 = $args[1] # No of threads

# args[2] # if 0 sequentially else parallel
# args[3] # matrix input filename
# args[4] # matrix output filename
# args[5] # test matrix filename
# args[6] # kernel filename
# args[7] # N 
# args[8] # M 
# args[9] # n 
# args[10] # m 

$nrRuns = $args[11] # No of runs




# Executare class Java

$suma = 0

for ($i = 0; $i -lt $nrRuns; $i++){
    Write-Host "Rulare" ($i+1)
    $a = java $args[0] $args[0] $args[1] $args[2] $args[3] $args[4] $args[5] $args[6] $args[7] $args[8] $args[9] $args[10] # rulare class java
    Write-Host $a
    $suma += $a
    Write-Host ""
}
$media = $suma / $i
#Write-Host $suma
Write-Host "Timp de executie mediu:" $media

# Creare fisier .csv
if (!(Test-Path outJ.csv)){
    New-Item outJ.csv -ItemType File
    #Scrie date in csv
    Set-Content outJ.csv 'Tip Matrice,Nr threads,Timp executie'
}

# Append
# Add-Content outJ.csv ",$($args[1]),$($media)"
Add-Content outJ.csv "N=$($args[7]) M=$($args[8]) n=$($args[9]) m=$($args[10]),$($args[1]),$($media)"
# Ejecutar desde la raiz del proyecto (PowerShell).
$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\*.java
if ($LASTEXITCODE -ne 0) { throw 'Fallo la compilacion' }
foreach ($clase in @('PruebaInicial', 'PruebaControladores', 'PruebaBuzon')) {
    java -cp out $clase
    if ($LASTEXITCODE -ne 0) { throw "Fallo $clase" }
}
foreach ($archivo in @('config.txt','pruebas/config_umbral1.txt','pruebas/config_contencion.txt','pruebas/config_sin_contencion.txt')) {
    Write-Host "Probando $archivo"
    java -Dcaso3.prueba.duracionMs=60 -cp out Main $archivo
    if ($LASTEXITCODE -ne 0) { throw "Fallo $archivo" }
}
Write-Host 'Todas las pruebas terminaron correctamente.'

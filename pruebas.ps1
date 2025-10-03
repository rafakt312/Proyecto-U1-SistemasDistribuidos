# pruebas.ps1 - Script de pruebas para el proyecto de morfología

# Configuración
$JAR = "target\morfologia-jar-with-dependencies.jar"
$IMAGEN = "lena.png"  # Cambia esto por tu imagen

# Verifica que exista el JAR
if (-not (Test-Path $JAR)) {
    Write-Host "ERROR: No existe el JAR. Ejecuta 'mvn clean package' primero" -ForegroundColor Red
    exit
}

# Verifica que exista la imagen
if (-not (Test-Path $IMAGEN)) {
    Write-Host "ERROR: No existe $IMAGEN" -ForegroundColor Red
    exit
}

Write-Host "`n=== INICIANDO PRUEBAS ===" -ForegroundColor Green

# Crea carpeta de resultados
New-Item -ItemType Directory -Force -Path "resultados" | Out-Null

# Array para guardar resultados
$resultados = @()

# PRUEBA 1: Erosión secuencial con todos los elementos estructurantes
Write-Host "`n[1/15] Erosión secuencial - Cuadrado 3x3" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op erosion --se 1 --edge pad --in $IMAGEN --out resultados\erosion_seq_cuadrado.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Erosion"; SE="Cuadrado3x3"; Hilos="-"; Tiempo=$tiempo}

Write-Host "[2/15] Erosión secuencial - Cruz 3x3" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op erosion --se 2 --edge pad --in $IMAGEN --out resultados\erosion_seq_cruz.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Erosion"; SE="Cruz3x3"; Hilos="-"; Tiempo=$tiempo}

Write-Host "[3/15] Erosión secuencial - X 3x3" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op erosion --se 3 --edge pad --in $IMAGEN --out resultados\erosion_seq_x.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Erosion"; SE="X3x3"; Hilos="-"; Tiempo=$tiempo}

Write-Host "[4/15] Erosión secuencial - Línea H 1x3" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op erosion --se 4 --edge pad --in $IMAGEN --out resultados\erosion_seq_linea.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Erosion"; SE="LineaH1x3"; Hilos="-"; Tiempo=$tiempo}

Write-Host "[5/15] Erosión secuencial - Diamante 5x5" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op erosion --se 5 --edge pad --in $IMAGEN --out resultados\erosion_seq_diamante.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Erosion"; SE="Diamante5x5"; Hilos="-"; Tiempo=$tiempo}

# PRUEBA 2: Dilatación secuencial
Write-Host "`n[6/15] Dilatación secuencial - Cuadrado 3x3" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op dilatacion --se 1 --edge pad --in $IMAGEN --out resultados\dilatacion_seq_cuadrado.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Dilatacion"; SE="Cuadrado3x3"; Hilos="-"; Tiempo=$tiempo}

# PRUEBA 3: Erosión paralela con diferentes hilos (solo elemento 1)
Write-Host "`n[7/15] Erosión paralela - 2 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op erosion --se 1 --edge pad --threads 2 --in $IMAGEN --out resultados\erosion_par_2h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Erosion"; SE="Cuadrado3x3"; Hilos="2"; Tiempo=$tiempo}

Write-Host "[8/15] Erosión paralela - 4 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op erosion --se 1 --edge pad --threads 4 --in $IMAGEN --out resultados\erosion_par_4h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Erosion"; SE="Cuadrado3x3"; Hilos="4"; Tiempo=$tiempo}

Write-Host "[9/15] Erosión paralela - 8 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op erosion --se 1 --edge pad --threads 8 --in $IMAGEN --out resultados\erosion_par_8h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Erosion"; SE="Cuadrado3x3"; Hilos="8"; Tiempo=$tiempo}

Write-Host "[10/15] Erosión paralela - 16 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op erosion --se 1 --edge pad --threads 16 --in $IMAGEN --out resultados\erosion_par_16h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Erosion"; SE="Cuadrado3x3"; Hilos="16"; Tiempo=$tiempo}

# PRUEBA 4: Dilatación paralela
Write-Host "`n[11/15] Dilatación paralela - 2 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op dilatacion --se 1 --edge pad --threads 2 --in $IMAGEN --out resultados\dilatacion_par_2h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Dilatacion"; SE="Cuadrado3x3"; Hilos="2"; Tiempo=$tiempo}

Write-Host "[12/15] Dilatación paralela - 4 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op dilatacion --se 1 --edge pad --threads 4 --in $IMAGEN --out resultados\dilatacion_par_4h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Dilatacion"; SE="Cuadrado3x3"; Hilos="4"; Tiempo=$tiempo}

Write-Host "[13/15] Dilatación paralela - 8 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op dilatacion --se 1 --edge pad --threads 8 --in $IMAGEN --out resultados\dilatacion_par_8h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Dilatacion"; SE="Cuadrado3x3"; Hilos="8"; Tiempo=$tiempo}

Write-Host "[14/15] Dilatación paralela - 16 hilos" -ForegroundColor Cyan
$output = java -jar $JAR --mode par --op dilatacion --se 1 --edge pad --threads 16 --in $IMAGEN --out resultados\dilatacion_par_16h.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Paralelo"; Op="Dilatacion"; SE="Cuadrado3x3"; Hilos="16"; Tiempo=$tiempo}

# PRUEBA 5: Comparación de políticas de borde
Write-Host "`n[15/15] Comparación de políticas de borde" -ForegroundColor Cyan
$output = java -jar $JAR --mode seq --op erosion --se 1 --edge ignore --in $IMAGEN --out resultados\erosion_edge_ignore.png --bench
$tiempo = ($output | Select-String "Tiempo promedio.*: ([\d.]+)" | ForEach-Object { $_.Matches.Groups[1].Value })
$resultados += [PSCustomObject]@{Modo="Secuencial"; Op="Erosion"; SE="Cuadrado3x3(ignore)"; Hilos="-"; Tiempo=$tiempo}

# Exporta resultados a CSV
$resultados | Export-Csv -Path "resultados\resultados.csv" -NoTypeInformation -Encoding UTF8
Write-Host "`n=== PRUEBAS COMPLETADAS ===" -ForegroundColor Green
Write-Host "Resultados guardados en: resultados\resultados.csv" -ForegroundColor Yellow
Write-Host "Imágenes generadas en: resultados\" -ForegroundColor Yellow

# Muestra tabla de resultados
Write-Host "`n=== RESUMEN DE RESULTADOS ===" -ForegroundColor Green
$resultados | Format-Table -AutoSize
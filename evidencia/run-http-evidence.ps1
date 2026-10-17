# Evidencia reproducible: levantamiento de la app + peticiones HTTP reales
# Uso:  powershell -ExecutionPolicy Bypass -File evidencia\run-http-evidence.ps1
$ErrorActionPreference = 'Continue'

$base = 'http://localhost:8080'
$ts = Get-Date -Format 'yyyyMMddHHmmss'
$correo1 = "rubrica_${ts}_a@utng.mx"
$correo2 = "rubrica_${ts}_b@utng.mx"
$pass = 'Password123!'
$logFile = Join-Path $PSScriptRoot 'pruebas-http.txt'
$jsonFile = Join-Path $PSScriptRoot 'pruebas-http-resumen.json'

$lineas = New-Object System.Collections.Generic.List[string]
$resumen = New-Object System.Collections.Generic.List[object]
$script:ok = 0
$script:mal = 0

function Add-Linea($texto) {
    $lineas.Add($texto)
    Write-Host $texto
}

function Invoke-Api($metodo, $ruta, $cuerpo, $token) {
    $headers = @{}
    if ($token) { $headers['Authorization'] = "Bearer $token" }
    $params = @{ Method = $metodo; Uri = "$base$ruta"; Headers = $headers; UseBasicParsing = $true }
    if ($null -ne $cuerpo) {
        $params['ContentType'] = 'application/json'
        $params['Body'] = $cuerpo
    }
    try {
        $r = Invoke-WebRequest @params
        return @{ Status = [int]$r.StatusCode; Body = [string]$r.Content }
    } catch {
        $resp = $_.Exception.Response
        if ($null -ne $resp) {
            $sr = New-Object System.IO.StreamReader($resp.GetResponseStream())
            $content = $sr.ReadToEnd()
            return @{ Status = [int]$resp.StatusCode; Body = $content }
        }
        return @{ Status = 0; Body = $_.Exception.Message }
    }
}

function Paso($descripcion, $metodo, $ruta, $cuerpo, $token, $esperado) {
    $r = Invoke-Api $metodo $ruta $cuerpo $token
    $estado = if ($r.Status -eq $esperado) { 'PASS' } else { 'FAIL' }
    if ($r.Status -eq $esperado) { $script:ok++ } else { $script:mal++ }
    $cuerpoCorto = $r.Body
    if ($cuerpoCorto.Length -gt 400) { $cuerpoCorto = $cuerpoCorto.Substring(0, 400) + '...' }
    Add-Linea ("[{0}] {1} {2} -> esperado {3}, obtenido {4}" -f $estado, $metodo, $ruta, $esperado, $r.Status)
    Add-Linea ("        -> {0}" -f $cuerpoCorto)
    $resumen.Add([pscustomobject]@{
        caso = $descripcion; metodo = $metodo; ruta = $ruta; esperado = $esperado;
        obtenido = $r.Status; resultado = $estado
    })
    return $r
}

Add-Linea "==================================================================="
Add-Linea "EVIDENCIA DE PRUEBAS HTTP REALES - API Administracion de Usuarios"
Add-Linea ("Fecha: {0}" -f (Get-Date))
Add-Linea ("Servidor: {0}" -f $base)
Add-Linea "==================================================================="
Add-Linea ""

# 1) Levantar la aplicacion
Add-Linea ">> Levantando la aplicacion (gradlew bootRun)..."
$stdout = Join-Path $PSScriptRoot 'bootrun-salida.log'
$stderr = Join-Path $PSScriptRoot 'bootrun-error.log'
$proc = Start-Process -FilePath (Join-Path (Get-Location) 'gradlew.bat') `
    -ArgumentList 'bootRun', '--console=plain' -PassThru -NoNewWindow `
    -RedirectStandardOutput $stdout -RedirectStandardError $stderr

$listo = $false
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep -Seconds 3
    $ping = Invoke-Api 'GET' '/actuator/health' $null $null
    if ($ping.Status -ne 0) { $listo = $true; break }
}
if (-not $listo) {
    Add-Linea "!! La aplicacion no respondio en el tiempo esperado. Revise $stdout"
} else {
    Add-Linea ">> Aplicacion lista. Ejecutando casos..."
}
Add-Linea ""

# 2) Casos de prueba
# 401 - credenciales invalidas (usuario inexistente)
Paso "Login con credenciales inexistentes" 'POST' '/auth/login' (ConvertTo-Json @{ correo = $correo1; password = $pass }) $null 401 | Out-Null

# 201 - crear usuario valido
$r = Paso "Crear usuario valido" 'POST' '/usuarios' (ConvertTo-Json @{ correo = $correo1; password = $pass; rol = 'ROLE_USER' }) $null 201
$id1 = ($r.Body | ConvertFrom-Json).data.id

# 409 - usuario duplicado
Paso "Crear usuario duplicado" 'POST' '/usuarios' (ConvertTo-Json @{ correo = $correo1; password = $pass }) $null 409 | Out-Null

# 400 - validacion Bean Validation (correo invalido y password debil)
Paso "Crear usuario con datos invalidos" 'POST' '/usuarios' (ConvertTo-Json @{ correo = 'no-es-correo'; password = 'corta' }) $null 400 | Out-Null

# 200 - login correcto -> token
$r = Paso "Login correcto" 'POST' '/auth/login' (ConvertTo-Json @{ correo = $correo1; password = $pass }) $null 200
$token1 = ($r.Body | ConvertFrom-Json).data.token
Add-Linea ("        token recibido: {0}..." -f $token1.Substring(0, [Math]::Min(25, $token1.Length)))
Add-Linea ""

# 401 - endpoint protegido sin token
Paso "Consultar detalle sin token" 'POST' '/usuarios/detalle' (ConvertTo-Json @{ id = $id1 }) $null 401 | Out-Null

# 405 - metodo no soportado (GET /usuarios)
Paso "Metodo GET no soportado" 'GET' '/usuarios' $null $token1 405 | Out-Null

# 200 - busqueda dinamica
Paso "Busqueda dinamica por correo" 'POST' '/usuarios/buscar' (ConvertTo-Json @{ correo = 'rubrica' }) $token1 200 | Out-Null

# 200 - detalle por id (via RequestBody, sin PathVariable)
Paso "Consultar detalle por RequestBody" 'POST' '/usuarios/detalle' (ConvertTo-Json @{ id = $id1 }) $token1 200 | Out-Null

# 404 - recurso no encontrado
Paso "Consultar id inexistente" 'POST' '/usuarios/detalle' (ConvertTo-Json @{ id = 999999 }) $token1 404 | Out-Null

# 201 - segundo usuario para probar 403
$r = Paso "Crear segundo usuario" 'POST' '/usuarios' (ConvertTo-Json @{ correo = $correo2; password = $pass }) $null 201
$id2 = ($r.Body | ConvertFrom-Json).data.id

# 403 - un usuario normal intenta modificar a otro
Paso "Modificar usuario ajeno sin permisos" 'PUT' '/usuarios' (ConvertTo-Json @{ id = $id2; correo = $correo2 }) $token1 403 | Out-Null

# 200 - cambio de estatus propio
Paso "Cambiar estatus propio" 'PATCH' '/usuarios/estatus' (ConvertTo-Json @{ id = $id1; activo = $true }) $token1 200 | Out-Null

# 200 - baja logica (DELETE con RequestBody)
Paso "Baja logica de usuario" 'DELETE' '/usuarios' (ConvertTo-Json @{ id = $id1 }) $token1 200 | Out-Null

Add-Linea ""
Add-Linea "==================================================================="
Add-Linea ("RESULTADO: {0} PASS / {1} FAIL" -f $script:ok, $script:mal)
Add-Linea "==================================================================="

# 3) Detener la aplicacion
$lineas | Out-File -FilePath $logFile -Encoding UTF8
$resumen | ConvertTo-Json -Depth 4 | Out-File -FilePath $jsonFile -Encoding UTF8

$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($conn) { Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue }
Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue

Write-Host ""
Write-Host ("Evidencia guardada en: {0}" -f $logFile)
Write-Host ("Resumen JSON en:       {0}" -f $jsonFile)

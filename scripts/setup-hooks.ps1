# Script de configuración de Git Hooks para Windows PowerShell
Write-Host "Configurando Git Hooks para AquaPort Enterprise..." -ForegroundColor Cyan
git config core.hooksPath .githooks
if ($LASTEXITCODE -eq 0) {
    Write-Host "Hooks configurados exitosamente en '.githooks'." -ForegroundColor Green
    Write-Host "  * commit-msg: Valida estándares de Conventional Commits" -ForegroundColor Gray
    Write-Host "  * pre-commit: Bloquea commits directos a ramas protegidas" -ForegroundColor Gray
} else {
    Write-Host "Error al configurar core.hooksPath." -ForegroundColor Red
}

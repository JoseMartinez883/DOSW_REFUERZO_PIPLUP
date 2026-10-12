#!/usr/bin/env bash
# Script de configuración de Git Hooks para Linux/macOS/Git Bash
echo "Configurando Git Hooks para AquaPort Enterprise..."
git config core.hooksPath .githooks
chmod +x .githooks/*
if [ $? -eq 0 ]; then
    echo "Hooks configurados exitosamente en '.githooks'."
else
    echo "Error al configurar core.hooksPath."
fi

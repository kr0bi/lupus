# Development Guide

## Sviluppo Locale con Docker

### Setup Iniziale

```bash
# Avvia tutti i servizi in modalità sviluppo
docker-compose -f docker-compose.dev.yml up -d

# Oppure avvia solo alcuni servizi
docker-compose -f docker-compose.dev.yml up -d db backend
```

### Frontend Development

Il frontend usa **volume mounts** per montare il codice sorgente nel container. Ogni modifica che fai ai file viene rilevata automaticamente da Expo Metro bundler e l'app si ricarica senza dover ribuilare il container.

**Porte esposte:**
- `8081` - Expo Metro bundler
- `19000` - Expo DevTools
- `19001` - Expo Metro bundler (alternativa)
- `19002` - Expo web

**Workflow:**
1. Modifica i file in `frontend/`
2. Le modifiche vengono rilevate automaticamente
3. L'app si ricarica nel browser/emulatore

```bash
# Visualizza i log del frontend
docker-compose -f docker-compose.dev.yml logs -f frontend

# Riavvia il frontend (se necessario)
docker-compose -f docker-compose.dev.yml restart frontend
```

### Backend Development

Il backend usa **Spring Boot DevTools** e **volume mounts** per rilevare le modifiche. Quando modifichi un file Java:
1. Maven ricompila automaticamente il file
2. Spring Boot DevTools riavvia l'applicazione (fast restart)

**Porte esposte:**
- `8080` - API REST
- `5005` - Debug port (per collegare il debugger dell'IDE)

**Workflow:**
1. Modifica i file in `backend/src/`
2. Spring Boot DevTools rileva le modifiche
3. L'app si riavvia automaticamente (2-5 secondi)

```bash
# Visualizza i log del backend
docker-compose -f docker-compose.dev.yml logs -f backend

# Riavvia il backend manualmente (se necessario)
docker-compose -f docker-compose.dev.yml restart backend
```

### Debug Remoto (Backend)

Puoi collegare il debugger del tuo IDE alla porta `5005`:

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Add New Configuration → Remote JVM Debug
3. Host: `localhost`, Port: `5005`
4. Start debugging

**VS Code:**
Aggiungi questa configurazione in `.vscode/launch.json`:
```json
{
  "type": "java",
  "name": "Debug Spring Boot",
  "request": "attach",
  "hostName": "localhost",
  "port": 5005
}
```

### Database

PostgreSQL è condiviso tra dev e prod:
- Host: `localhost:5432`
- Database: `lupus`
- User: `lupus`
- Password: `lupus`

```bash
# Accedi al database
docker-compose -f docker-compose.dev.yml exec db psql -U lupus -d lupus

# Visualizza i log del database
docker-compose -f docker-compose.dev.yml logs -f db
```

### Comandi Utili

```bash
# Stop tutti i servizi
docker-compose -f docker-compose.dev.yml down

# Stop e rimuovi i volumi (attenzione: cancella il database!)
docker-compose -f docker-compose.dev.yml down -v

# Rebuild un singolo servizio
docker-compose -f docker-compose.dev.yml build backend
docker-compose -f docker-compose.dev.yml up -d backend

# Visualizza lo stato dei container
docker-compose -f docker-compose.dev.yml ps

# Esegui comandi nel container
docker-compose -f docker-compose.dev.yml exec backend bash
docker-compose -f docker-compose.dev.yml exec frontend sh
```

## Produzione

Per la build di produzione usa il `docker-compose.yml` standard:

```bash
# Build e avvia in produzione
docker-compose up -d --build

# Il frontend sarà disponibile su http://localhost:3000
# Il backend sarà disponibile su http://localhost:8080
```

## Differenze Dev vs Prod

| Feature | Dev | Prod |
|---------|-----|------|
| Frontend | Expo Dev Server | Build statico + Nginx |
| Backend | Maven + DevTools | JAR precompilato |
| Hot Reload | ✅ Si | ❌ No |
| Volume Mounts | ✅ Codice sorgente | ❌ Nessuno |
| Debug Port | ✅ 5005 | ❌ No |
| Ottimizzazione | ❌ No | ✅ Si |

## Troubleshooting

### Le modifiche al frontend non vengono rilevate
```bash
docker-compose -f docker-compose.dev.yml restart frontend
```

### Le modifiche al backend non vengono rilevate
1. Verifica che Spring Boot DevTools sia attivo nei log
2. Riavvia il backend se necessario
```bash
docker-compose -f docker-compose.dev.yml restart backend
```

### Errori di cache Maven
```bash
# Rimuovi la cache Maven e ribuildi
docker-compose -f docker-compose.dev.yml down
docker volume rm lupus-backend_maven-cache
docker-compose -f docker-compose.dev.yml up -d --build backend
```

### Port già in uso
```bash
# Trova il processo che usa la porta
lsof -i :8080
# Oppure
netstat -tulpn | grep 8080

# Kill il processo se necessario
kill -9 <PID>
```

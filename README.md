# Style Studio

Client-seitige Fabric-Mod für Minecraft 1.21.11.

## Style-Browser (Hauptfunktion)
Öffnen mit `/stylestudio` im Spiel oder über ModMenu → Style Studio → „Style-Browser".
Pro Ziel (Obsidian, Respawn-Anker, Schild, Rüstungen, Schwerter, …) wählst du einen Stil
(Amethyst, Blutrot, Feuer, Gold, Smaragd, Eis, Ozean, Neon-Pink, Schwarz-Weiß, Schatten).
„Gilt für" schaltet zwischen Auswahl, ganzer Kategorie und Alles um.
„Anwenden" erzeugt ein echtes Resource Pack `resourcepacks/stylestudio-styles` aus den
Vanilla-Texturen und lädt die Ressourcen neu. Beim ersten Mal das Pack einmalig unter
Optionen → Ressourcenpakete aktivieren.

## Extras (ModMenu)
- Chat: Uhrzeit, Präfix, Farben
- HUD: FPS/Koordinaten-Panel, Fadenkreuz
- Menü: Farb-Overlay auf Titelbildschirm

Build: `gradlew build` → `build/libs/stylestudio-styles-1.7.0.jar`
(oder per GitHub Actions: Reiter „Actions" → Artifact `style-studio`)

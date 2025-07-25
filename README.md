# MATTYGLASK Version 0.4

Programmer : Corbin Duncan  
Creative Director : Wyatt Latham

## Custom JRE image included built with jdeps:

- jdeps --module-path bin -s -recursive -summary MattyGlask0.4.jar
- jlink --module-path "$env:JAVA_HOME\jmods;out" --add-modules <modules separated by commas(nospace)> --output MTGLJRE

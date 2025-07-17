# CUSTOM JRE IMAGE

- jdeps --module-path bin -s -recursive -summary MattyGlask0.4.jar
- jlink --module-path "$env:JAVA_HOME\jmods;out" --add-modules <modules separated by commas(nospace)> --output MTGLJRE

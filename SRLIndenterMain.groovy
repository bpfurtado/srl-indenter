def file = new File("sample.srl");

def origLines = file.readLines();
def lines = handleLineTerminators(origLines)//splitSemiColon(origLines);
def linesString = lines.join("\n")
lines.clear()
linesString.eachLine { line ->
    lines.add(line)
}
lines = indentByCurlyBrace(lines)

def newFile = new File("sample-out.srl");
newFile.write(lines.join("\n"));

println "Done!"

def List<String> indentByCurlyBrace(List<String> lines) {
    List<String> newLines = []
    def indent = 0
    lines.eachWithIndex { line, index ->
        println "I($indent): [$line] - hasClose[" + line.contains("}") + "] - hasOpen[" + line.contains("{") + "]"
        def indentSufix = ''// " // Indent: $indent"
        if (!isComment(line) && line.contains("{")) {
            newLines.add("    " * indent + line + indentSufix)
            indent++
        } else if (!isComment(line) && line.contains("}")) {
            indent--
            newLines.add("    " * indent + line + indentSufix)
        } else {
            newLines.add("    " * indent + line + indentSufix)
        }
    }
    return newLines
}

def List<String> handleLineTerminators(List<String> lines) {
    def newLines = []
    lines.eachWithIndex { line, index ->
        if (isComment(line)) {
            newLines << line.trim()
        } else {
            def newLine = ""
            println "Line [$line]"
            for(idx = 0; idx < line.length(); idx++) {
                def currentChar = line.charAt(idx)
                print "($currentChar)"
                if (currentChar == "{") {
                    print "*1"
                    newLine += "{\n"
                } else if (currentChar == "}") {
                    print "*2"
                    def previousChar = null;
                    if(idx != 0) previousChar = line.charAt(idx - 1)
                    if(previousChar!=null&&previousChar == "\n") {
                        newLine += "\n}\n"
                    } else {
                        newLine += "}\n"
                    }
                } else if (currentChar == ";") {
                    print "*3"
                    newLine += ";\n"
                } else {
                    newLine += currentChar
                }
            }
            println ""

            //split over newlines and trim each line
            finalLine = ""
            newLine.eachLine { innerLine ->
                finalLine += innerLine.trim() + "\n"
            }
            finalLine = finalLine.trim()
            println "New Line:\nStart\n$finalLine\nEnd"
            newLines << finalLine
        }
    }
    return newLines
}

def Boolean isComment(String line) {
    return line.trim().startsWith("//")
}
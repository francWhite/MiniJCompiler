package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.type.BooleanType;
import ch.hslu.cobau.minij.ast.type.IntegerType;
import ch.hslu.cobau.minij.ast.type.StringType;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;


import java.io.IOException;

public class MiniJAstBuilderTest {
    @Test
    public void globalDeclarations() {
        var input =
                "int number1;" + System.lineSeparator() +
                "int number2;";

        var program = createAst(input);

        var globals = program.getGlobals();
        assertThat(globals).hasSize(2);
        assertThat(program.getProcedures()).isEmpty();
        assertThat(program.getRecords()).isEmpty();

        var firstDecleration = globals.get(0);
        assertThat(firstDecleration.getIdentifier()).isEqualTo("number1");
        assertThat(firstDecleration.getType()).isInstanceOf(IntegerType.class);

        var secondDeclaration = globals.get(1);
        assertThat(secondDeclaration.getIdentifier()).isEqualTo("number2");
        assertThat(secondDeclaration.getType()).isInstanceOf(IntegerType.class);
    }

    @Test
    public void globalDeclarations_File() {
        var input = readInputFile("test.minij");
        var program = createAst(input);

        var globals = program.getGlobals();
        assertThat(globals).hasSize(2);
        assertThat(program.getProcedures()).isEmpty();
        assertThat(program.getRecords()).isEmpty();

        var firstDecleration = globals.get(0);
        assertThat(firstDecleration.getIdentifier()).isEqualTo("number1");
        assertThat(firstDecleration.getType()).isInstanceOf(IntegerType.class);

        var secondDeclaration = globals.get(1);
        assertThat(secondDeclaration.getIdentifier()).isEqualTo("number2");
        assertThat(secondDeclaration.getType()).isInstanceOf(IntegerType.class);
    }

    @Test
    public void recordDeclaration(){
        var input =
                "record Auto\n" +
                    "int Jahr;\n" +
                    "string Modell;\n" +
                    "boolean Verfuegbar;\n" +
                "end;";

        var program = createAst(input);

        var records = program.getRecords();
        assertThat(records).hasSize(1);

        var record = records.get(0);
        assertThat(record.getIdentifier()).isEqualTo("Auto");

        var declarations = record.getDeclarations();
        assertThat(declarations)
                .hasSize(3)
                .anyMatch(d -> d.getIdentifier().equals("Jahr") && d.getType().getClass() == IntegerType.class)
                .anyMatch(d -> d.getIdentifier().equals("Modell") && d.getType().getClass() == StringType.class)
                .anyMatch(d -> d.getIdentifier().equals("Verfuegbar") && d.getType().getClass() == BooleanType.class);
    }

    @Test
    public void recordDeclarationAndGlobalDeclarations(){
        var input =
                "int x;\n" +
                "record Auto\n" +
                    "int Jahr;\n" +
                    "string Modell;\n" +
                    "boolean Verfuegbar;\n" +
                "end;\n" +
                "int y;" ;

        var program = createAst(input);

        var records = program.getRecords();
        assertThat(records).hasSize(1);

        var record = records.get(0);
        assertThat(record.getIdentifier()).isEqualTo("Auto");

        var recordDeclarations = record.getDeclarations();
        assertThat(recordDeclarations)
                .hasSize(3)
                .anyMatch(d -> d.getIdentifier().equals("Jahr") && d.getType().getClass() == IntegerType.class)
                .anyMatch(d -> d.getIdentifier().equals("Modell") && d.getType().getClass() == StringType.class)
                .anyMatch(d -> d.getIdentifier().equals("Verfuegbar") && d.getType().getClass() == BooleanType.class);

        var declarations = program.getGlobals();
        assertThat(declarations)
                .hasSize(2)
                .anyMatch(d -> d.getIdentifier().equals("x"))
                .anyMatch(d -> d.getIdentifier().equals("y"));
    }

    private Program createAst(String input) {
        MiniJLexer miniJLexer = new MiniJLexer(CharStreams.fromString(input));
        CommonTokenStream commonTokenStream = new CommonTokenStream(miniJLexer);
        MiniJParser miniJParser = new MiniJParser(commonTokenStream);
        MiniJParser.UnitContext unitContext = miniJParser.unit();

        MiniJAstBuilder miniJAstBuilder = new MiniJAstBuilder();
        return miniJAstBuilder.visit(unitContext);
    }

    private String readInputFile(String name) {
        var resources = this.getClass().getResourceAsStream(name);
        try {
            return new String(resources.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

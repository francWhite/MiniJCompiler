package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.type.IntegerType;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class MiniJAstBuilderTest {
    @Test
    public void globalDeclarations() {
        var input =
                "int number1;" + System.lineSeparator() +
                "int number2;";

        var program = createAst(input);

        var globals = program.getGlobals();
        Assert.assertEquals(2, globals.size());

        var firstDecleration = globals.get(0);
        Assert.assertEquals("number1", firstDecleration.getIdentifier());
        Assert.assertEquals(IntegerType.class, firstDecleration.getType().getClass());

        var secondDeclaration = globals.get(1);
        Assert.assertEquals("number2", secondDeclaration.getIdentifier());
        Assert.assertEquals(IntegerType.class, secondDeclaration.getType().getClass());
    }

    @Test
    public void globalDeclarations_File() {
        var input = readInputFile("test.minij");
        var program = createAst(input);

        var globals = program.getGlobals();
        Assert.assertEquals(2, globals.size());

        var firstDecleration = globals.get(0);
        Assert.assertEquals("number1", firstDecleration.getIdentifier());
        Assert.assertEquals(IntegerType.class, firstDecleration.getType().getClass());

        var secondDeclaration = globals.get(1);
        Assert.assertEquals("number2", secondDeclaration.getIdentifier());
        Assert.assertEquals(IntegerType.class, secondDeclaration.getType().getClass());
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

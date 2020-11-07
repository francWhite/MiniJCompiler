package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.constants.IntegerConstant;
import ch.hslu.cobau.minij.ast.constants.StringConstant;
import ch.hslu.cobau.minij.ast.constants.TrueConstant;
import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;
import ch.hslu.cobau.minij.ast.type.ArrayType;
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
    public void globalArrayDeclarations() {
        var input =
                "int[] number1;";

        var program = createAst(input);

        var globals = program.getGlobals();
        assertThat(globals).hasSize(1);

        var firstDecleration = globals.get(0);
        assertThat(firstDecleration.getIdentifier()).isEqualTo("number1");
        assertThat(firstDecleration.getType()).isInstanceOf(ArrayType.class);

        var arrayType = (ArrayType) firstDecleration.getType();
        assertThat(arrayType.getType()).isInstanceOf(IntegerType.class);
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
                .anyMatch(d -> d.getIdentifier().equals("Jahr") && d.getType() instanceof IntegerType)
                .anyMatch(d -> d.getIdentifier().equals("Modell") && d.getType() instanceof  StringType)
                .anyMatch(d -> d.getIdentifier().equals("Verfuegbar") && d.getType()instanceof BooleanType);

        var declarations = program.getGlobals();
        assertThat(declarations)
                .hasSize(2)
                .anyMatch(d -> d.getIdentifier().equals("x"))
                .anyMatch(d -> d.getIdentifier().equals("y"));
    }

    @Test
    public void simpleProcedure(){
        var input =
                "procedure test1(ref int number1, int number2)\n"+
                    "int number3;\n" +
                    "begin\n"+
                    "return;\n"+
                    "end\n";

        var program = createAst(input);
        assertThat(program.getProcedures()).hasSize(1);

        var procedure = program.getProcedures().get(0);
        assertThat(procedure.getIdentifier()).isEqualTo("test1");
        assertThat(procedure.getFormalParameters()).hasSize(2);
        assertThat(procedure.getDeclarations()).hasSize(1);
        assertThat(procedure.getStatements()).hasSize(1);

        var parameter = procedure.getFormalParameters().get(0);
        assertThat(parameter.getIdentifier()).isEqualTo("number1");
        assertThat(parameter.getType()).isInstanceOf(IntegerType.class);
        assertThat(parameter.isByReference()).isEqualTo(true);

        var parameter2 = procedure.getFormalParameters().get(1);
        assertThat(parameter2.getIdentifier()).isEqualTo("number2");
        assertThat(parameter2.getType()).isInstanceOf(IntegerType.class);
        assertThat(parameter2.isByReference()).isEqualTo(false);

        var declaration = procedure.getDeclarations().get(0);
        assertThat(declaration.getIdentifier()).isEqualTo("number3");
        assertThat(declaration.getType()).isInstanceOf(IntegerType.class);

        var statement = procedure.getStatements().get(0);
        assertThat(statement).isInstanceOf(ReturnStatement.class);
    }

    @Test
    public void assignment_setConstantValueToVariable(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "number = 5;\n"+
                        "end\n";

        var program = createAst(input);
        var statement = program.getProcedures().get(0).getStatements().get(0);

        assertThat(statement).isInstanceOf(AssignmentStatement.class);
        var assignmentStatement = (AssignmentStatement)statement;

        var left = assignmentStatement.getLeft();
        assertThat(left).isInstanceOf(VariableAccess.class);
        var variableAcces =(VariableAccess)left;
        assertThat(variableAcces.getIdentifier()).isEqualTo("number");

        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(IntegerConstant.class);
        var constant = (IntegerConstant)right;
        assertThat(constant.getValue()).isEqualTo(5);
    }

    @Test
    public void assignment_setConstantValueToField(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "object.number = 5;\n"+
                        "end\n";

        var program = createAst(input);
        var statement = program.getProcedures().get(0).getStatements().get(0);

        assertThat(statement).isInstanceOf(AssignmentStatement.class);
        var assignmentStatement = (AssignmentStatement)statement;

        //left side of assignment----------------------------------------------------
        var left = assignmentStatement.getLeft();
        assertThat(left).isInstanceOf(FieldAccess.class);

        var fieldAccess =(FieldAccess)left;
        assertThat(fieldAccess.getBase()).isInstanceOf(VariableAccess.class);

        var base = (VariableAccess)fieldAccess.getBase();
        assertThat(base.getIdentifier()).isEqualTo("object");

        var field = fieldAccess.getField();
        assertThat(field).isEqualTo("number");

        //right side of assignment--------------------------------------------------
        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(IntegerConstant.class);
        var constant = (IntegerConstant)right;
        assertThat(constant.getValue()).isEqualTo(5);
    }

    @Test
    public void assignment_setConstantValueToArray(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "object[1] = 5;\n"+
                        "end\n";

        var program = createAst(input);
        var statement = program.getProcedures().get(0).getStatements().get(0);

        assertThat(statement).isInstanceOf(AssignmentStatement.class);
        var assignmentStatement = (AssignmentStatement)statement;

        //left side of assignment----------------------------------------------------
        var left = assignmentStatement.getLeft();
        assertThat(left).isInstanceOf(ArrayAccess.class);

        var arrayAccess =(ArrayAccess)left;
        assertThat(arrayAccess.getBase()).isInstanceOf(VariableAccess.class);
        assertThat(arrayAccess.getIndexExpression()).isInstanceOf(IntegerConstant.class);

        var base = (VariableAccess)arrayAccess.getBase();
        assertThat(base.getIdentifier()).isEqualTo("object");

        var index = (IntegerConstant)arrayAccess.getIndexExpression();
        assertThat(index.getValue()).isEqualTo(1);

        //right side of assignment--------------------------------------------------
        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(IntegerConstant.class);
        var constant = (IntegerConstant)right;
        assertThat(constant.getValue()).isEqualTo(5);
    }

    @Test
    public void unaryExpression_preDecrement(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "object = --other;\n"+
                        "end\n";

        var program = createAst(input);
        var assignmentStatement = (AssignmentStatement)program.getProcedures().get(0).getStatements().get(0);

        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(UnaryExpression.class);

        var unaryExpression = (UnaryExpression)right;
        var expression = unaryExpression.getExpression();
        var operator = unaryExpression.getUnaryOperator();

        assertThat(expression).isInstanceOf(VariableAccess.class);
        assertThat(operator).isEqualTo(UnaryOperator.PRE_DECREMENT);

        var variabelAcces = (VariableAccess)expression;
        assertThat(variabelAcces.getIdentifier()).isEqualTo("other");
    }

    @Test
    public void complexAsignment(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "base.field.secondFiled[1] = \"stringValue\";\n"+
                        "end\n";

        var program = createAst(input);
        var assignmentStatement = (AssignmentStatement)program.getProcedures().get(0).getStatements().get(0);

        var right = (StringConstant)assignmentStatement.getRight();
        assertThat(right.getValue()).isEqualTo("stringValue");

        var arrayAccess = (ArrayAccess)assignmentStatement.getLeft();
        var arrayIndex = (IntegerConstant)arrayAccess.getIndexExpression();
        assertThat(arrayIndex.getValue()).isEqualTo(1);

        var field = (FieldAccess)arrayAccess.getBase();
        assertThat(field.getField()).isEqualTo("secondFiled");

        var baseOfField = (FieldAccess)field.getBase();
        assertThat(baseOfField.getField()).isEqualTo("field");

        var realBase = (VariableAccess)baseOfField.getBase();
        assertThat(realBase.getIdentifier()).isEqualTo("base");
    }

    @Test
    public void unaryExpression_Not(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "object = !other;\n"+
                        "end\n";

        var program = createAst(input);
        var assignmentStatement = (AssignmentStatement)program.getProcedures().get(0).getStatements().get(0);

        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(UnaryExpression.class);

        var unaryExpression = (UnaryExpression)right;
        var operator = unaryExpression.getUnaryOperator();

        assertThat(operator).isEqualTo(UnaryOperator.NOT);
    }

    @Test
    public void unaryExpression_PostIncrement(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "object = other++;\n"+
                        "end\n";

        var program = createAst(input);
        var assignmentStatement = (AssignmentStatement)program.getProcedures().get(0).getStatements().get(0);

        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(UnaryExpression.class);

        var unaryExpression = (UnaryExpression)right;
        var operator = unaryExpression.getUnaryOperator();

        assertThat(operator).isEqualTo(UnaryOperator.POST_INCREMENT);
    }

    @Test
    public void binaryExpression_add(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "object = 1 + 2;\n"+
                        "end\n";

        var program = createAst(input);
        var assignmentStatement = (AssignmentStatement)program.getProcedures().get(0).getStatements().get(0);

        var right = assignmentStatement.getRight();
        assertThat(right).isInstanceOf(BinaryExpression.class);

        var binaryExpression = (BinaryExpression)right;
        var leftExpression = (IntegerConstant)binaryExpression.getLeft();
        var rightExpression = (IntegerConstant)binaryExpression.getRight();
        var operand = binaryExpression.getBinaryOperator();

        assertThat(leftExpression.getValue()).isEqualTo(1);
        assertThat(rightExpression.getValue()).isEqualTo(2);
        assertThat(operand).isEqualTo(BinaryOperator.PLUS);
    }

    @Test
    public void methodCallStatement(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                        "call(1, x);\n"+
                        "end\n";

        var program = createAst(input);
        var callStatement = (CallStatement)program.getProcedures().get(0).getStatements().get(0);

        assertThat(callStatement.getIdentifier()).isEqualTo("call");
        var firstParameter = (IntegerConstant)callStatement.getParameters().get(0);
        var secondParameter = (VariableAccess)callStatement.getParameters().get(1);

        assertThat(firstParameter.getValue()).isEqualTo(1);
        assertThat(secondParameter.getIdentifier()).isEqualTo("x");
    }

    @Test
    public void whileStatement(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                            "while(true)\n"+
                            "do\n"+
                                "call();\n"+
                            "end\n"+
                        "end\n";

        var program = createAst(input);

        var whileStatement = (WhileStatement)program.getProcedures().get(0).getStatements().get(0);
        assertThat(whileStatement.getExpression()).isInstanceOf(TrueConstant.class);

        var statement = whileStatement.getStatements().get(0);
        assertThat(statement).isInstanceOf(CallStatement.class);
    }

    @Test
    public void ifElsIfElseStatement(){
        var input =
                "procedure test()\n"+
                        "begin\n"+
                            "if(true)\n"+
                            "then\n"+
                                "call1();\n"+
                            "elsif(true)\n"+
                            "then\n"+
                                "call2();\n" +
                            "else\n"+
                                "call3();\n"+
                            "end\n"+
                        "end\n";

        var program = createAst(input);

        var ifStatement = (IfStatement)program.getProcedures().get(0).getStatements().get(0);
        var statement = (CallStatement)ifStatement.getStatements().get(0);
        assertThat(statement.getIdentifier()).isEqualTo("call1");

        var elseIfStatement =  (IfStatement)ifStatement.getElseBlock();
        statement = (CallStatement)elseIfStatement.getStatements().get(0);
        assertThat(statement.getIdentifier()).isEqualTo("call2");

        var elseBlock = (Block)elseIfStatement.getElseBlock();
        statement = (CallStatement)elseBlock.getStatements().get(0);
        assertThat(statement.getIdentifier()).isEqualTo("call3");
    }

    private Program createAst(String input) {
        MiniJLexer miniJLexer = new MiniJLexer(CharStreams.fromString(input));
        CommonTokenStream commonTokenStream = new CommonTokenStream(miniJLexer);
        MiniJParser miniJParser = new MiniJParser(commonTokenStream);
        MiniJParser.UnitContext unitContext = miniJParser.unit();

        MiniJAstBuilder miniJAstBuilder = new MiniJAstBuilder(new EnhancedConsoleErrorListener());
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

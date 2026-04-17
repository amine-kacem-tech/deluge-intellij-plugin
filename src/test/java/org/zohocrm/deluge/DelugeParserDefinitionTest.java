package org.zohocrm.deluge;

import com.intellij.lang.ParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Test suite for Deluge parser definition
 */
public class DelugeParserDefinitionTest extends BasePlatformTestCase {

    private DelugeParserDefinition parserDefinition;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parserDefinition = new DelugeParserDefinition();
    }

    /**
     * Test that the parser definition creates a lexer
     */
    public void testCreateLexer() {
        Project project = getProject();
        Lexer lexer = parserDefinition.createLexer(project);

        assertNotNull("Lexer should not be null", lexer);
        assertTrue("Should be DelugeLexerAdapter instance",
                  lexer instanceof DelugeLexerAdapter);
    }

    /**
     * Test that the parser definition creates a parser
     */
    public void testCreateParser() {
        Project project = getProject();
        com.intellij.lang.PsiParser parser = parserDefinition.createParser(project);

        assertNotNull("Parser should not be null", parser);
    }

    /**
     * Test that the file node type is correct
     */
    public void testGetFileNodeType() {
        IFileElementType fileNodeType = parserDefinition.getFileNodeType();

        assertNotNull("File node type should not be null", fileNodeType);
        assertEquals("File node type language should be Deluge",
                    DelugeLanguage.INSTANCE,
                    fileNodeType.getLanguage());
    }

    /**
     * Test that whitespace tokens are defined
     */
    public void testGetWhitespaceTokens() {
        TokenSet whitespaceTokens = parserDefinition.getWhitespaceTokens();
        assertNotNull("Whitespace tokens should not be null", whitespaceTokens);
    }

    /**
     * Test that comment tokens are defined
     */
    public void testGetCommentTokens() {
        TokenSet commentTokens = parserDefinition.getCommentTokens();
        assertNotNull("Comment tokens should not be null", commentTokens);
    }

    /**
     * Test that string literal tokens are defined
     */
    public void testGetStringLiteralElements() {
        TokenSet stringTokens = parserDefinition.getStringLiteralElements();
        assertNotNull("String literal tokens should not be null", stringTokens);
    }
}


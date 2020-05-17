<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <body>
                <h2>SANITARY CONDITION OF WORKSHOPS AND FACTORIES</h2>
                <h3>Table 1</h3>
                <table border="1">
                    <tr>
                        <th>Number</th>
                        <th>Question</th>
                        <th>Yes</th>
                        <th>No</th>
                        <th>Blank</th>
                        <th>Total</th>
                    </tr>
                    <xsl:for-each select="surveydata/questions/question">
                        <xsl:if test="position() != 12">
                            <tr>
                                <xsl:choose>
                                    <xsl:when test="position() = 13">
                                        <td>
                                            <xsl:value-of select="position()-1"/></td>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <td><xsl:value-of select="position()"/></td>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <td><xsl:value-of select="phrase"/></td>
                                <td><xsl:value-of select="answers/yes"/></td>
                                <td><xsl:value-of select="answers/no"/></td>
                                <td><xsl:value-of select="answers/blank"/></td>
                                <td><xsl:value-of select="answers/yes+answers/no+answers/blank"/></td>
                            </tr>
                        </xsl:if>
                    </xsl:for-each>
                </table>
                <h3>Table 2</h3>
                <table border="1">
                    <tr><th>Question</th>
                        <th>Sit</th>
                        <th>Stand</th>
                        <th>Blank</th>
                        <th>Optional</th>
                        <th>Total</th>
                    </tr>
                    <tr>
                        <xsl:for-each select="surveydata/questions/question">
                            <xsl:choose>
                                <xsl:when test="position() = 12">
                                    <td><xsl:value-of select="phrase"/></td>
                                    <td><xsl:value-of select="answers/sit"/></td>
                                    <td><xsl:value-of select="answers/stand"/></td>
                                    <td><xsl:value-of select="answers/blank"/></td>
                                    <td><xsl:value-of select="answers/optional"/></td>
                                    <td><xsl:value-of select="answers/sit+answers/stand+answers/blank+answers/optional"/></td>
                                </xsl:when>
                                <xsl:otherwise></xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>

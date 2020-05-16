<?xml version="1.0"?>

<xsl:transform version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/2000/svg"
>

<xsl:output method="xml" indent="yes"/>
    
<xsl:variable name="chart-top" select="30"/>
<xsl:variable name="chart-height" select="400"/>
<xsl:variable name="chart-bottom" ><xsl:value-of select="$chart-top+$chart-height"/> </xsl:variable>
<xsl:variable name="bar-width" select="30"/>
    
  <xsl:template match="/">
    <svg width="900" height="1000"> 
      <rect width="900" height="1000" fill="#e6e6e6"/>
        <g transform="translate(30,50)">
            <text font-size="16" x="120">SANITARY CONDITION OF WORKSHOPS AND FACTORIES</text>
            <!-- create axes -->
            <g>
            <line x1="50" x2="50" y1="{$chart-top}" y2="{$chart-height+30}" stroke="black"/> 
            <line x1="50" x2="600" y1="{$chart-height+$chart-top}" y2="{$chart-height+30}" stroke="black"/>
            
            <!-- label axes -->
            <text x="20" y="{$chart-top}" fill="black">100%</text>
            <text x="20" y="{$chart-top+210}" fill="black">50%</text>
                </g>
        <xsl:apply-templates select="surveydata"/>
        </g>
        <g>
            <text x="700" y="190" fill="black" font-size="14">Legend: Survey Responses</text>
            
        <rect x="700" y="200" fill="darkgrey" width="40" height="20"/>
            <text x="745" y="214" fill="black" font-size="12" vertical-align="middle">Blank</text>
            
            <rect x="700" y="230" fill="darkgreen" width="40" height="20"/>
            <text x="745" y="244" fill="black" font-size="12">No</text>
            
            <rect x="700" y="260" fill="purple" width="40" height="20"/>
            <text x="745" y="274" fill="black" font-size="12">Yes</text>
            
            </g>
      </svg>
    </xsl:template>
    
    
<xsl:template match="questions">
    <xsl:for-each select="./question">
        <xsl:sort select="./answers/yes" data-type="number" order="descending"/>
        <xsl:variable name="counter" select="position()"/>
        <xsl:apply-templates select="answers">
            <xsl:with-param name="counter" select="$counter"/>
        </xsl:apply-templates>
        
</xsl:for-each>
</xsl:template>

<xsl:template match="answers">
    <xsl:param name="counter"/>
    <xsl:if test="./yesno">
                    <xsl:variable name="question-number" select='concat("Q",$counter, ": ")'/>
                    <xsl:variable name="phrase"><xsl:value-of select="../phrase"/></xsl:variable>
                    <xsl:variable name="bar-label" select='concat($question-number, $phrase)'/>
            
                    <xsl:variable name="yes"><xsl:value-of select="./yesno/yes"/></xsl:variable>
                    <xsl:variable name="no"><xsl:value-of select="./yesno/no"/></xsl:variable>
                    <xsl:variable name="blank"><xsl:value-of select="./blank"/></xsl:variable>
                    <xsl:variable name="total"><xsl:value-of select="$yes+$no+$blank"/></xsl:variable>
        
                    <xsl:variable name="yes-ratio"><xsl:value-of select="$yes div $total"/></xsl:variable>
                    <xsl:variable name="no-ratio"><xsl:value-of select="$no div $total"/></xsl:variable>
                    <xsl:variable name="blank-ratio"><xsl:value-of select="$blank div $total"/></xsl:variable>
            
                    <xsl:variable name="yes-percentage"><xsl:value-of select='round($yes-ratio*100)'/> </xsl:variable>
                    <xsl:variable name="no-percentage"><xsl:value-of select='round($no-ratio*100)'/> </xsl:variable>
                    <xsl:variable name="blank-percentage"><xsl:value-of select='round($blank-ratio*100)'/> </xsl:variable>
        
                    <xsl:variable name="yes-bar-height"><xsl:value-of select='round($yes-ratio*$chart-height)'/></xsl:variable>
                    <xsl:variable name="no-bar-height"><xsl:value-of select='round($no-ratio*$chart-height)'/></xsl:variable>
                    <xsl:variable name="blank-bar-height"><xsl:value-of select='round($blank-ratio*$chart-height)'/></xsl:variable>
            
                    <g>
                        <!-- yes bar -->
                        <xsl:call-template name="yes">
                            <xsl:with-param name="counter" select="$counter"/>
                            <xsl:with-param name="yes-bar-height" select="$yes-bar-height"/>
                            <xsl:with-param name="yes-percentage" select="$yes-percentage"/>
                        </xsl:call-template>
                        
                        <!-- no bar -->
                        <xsl:call-template name="no">
                            <xsl:with-param name="counter" select="$counter"/>
                            <xsl:with-param name="yes-bar-height" select="$yes-bar-height"/>
                            <xsl:with-param name="no-bar-height" select="$no-bar-height"/>
                            <xsl:with-param name="no-percentage" select="$no-percentage"/>
                        </xsl:call-template>
                        
                        <!-- blank bar -->
                        <xsl:call-template name="blank">
                            <xsl:with-param name="counter" select="$counter"/>
                            <xsl:with-param name="yes-bar-height" select="$yes-bar-height"/>
                            <xsl:with-param name="no-bar-height" select="$no-bar-height"/>
                            <xsl:with-param name="blank-bar-height" select="$blank-bar-height"/>
                            <xsl:with-param name="blank-percentage" select="$blank-percentage"/>
                        </xsl:call-template>
                        
                        <text x="{45*$counter+35}" y="{$chart-bottom+5}" fill="black" font-size="12" writing-mode="tb"><xsl:value-of select="$bar-label" /></text>
                        </g>
</xsl:if>
    </xsl:template>

<xsl:template name="yes">
    <xsl:param name="counter"/>
    <xsl:param name="yes-bar-height"/>
    <xsl:param name="yes-percentage"/>
            <rect x="{45*$counter+20}" y="{$chart-bottom - $yes-bar-height}" fill="purple" width="{$bar-width}" height="{$yes-bar-height}"/>
            <text x="{45*$counter+35}" y="{$chart-bottom - ($yes-bar-height div 2)}" fill="white" font-size="10" text-anchor="middle"><xsl:value-of select="concat($yes-percentage,'%')"/></text>
</xsl:template>
    
<xsl:template name="no">
    <xsl:param name="counter"/>
    <xsl:param name="yes-bar-height"/>
    <xsl:param name="no-bar-height"/>
    <xsl:param name="no-percentage"/>
            <rect x="{45*$counter+20}" y="{$chart-bottom - $yes-bar-height - $no-bar-height}" fill="darkgreen" width="{$bar-width}" height="{$no-bar-height}"/>
            <text x="{45*$counter+35}" y="{$chart-bottom - $yes-bar-height -($no-bar-height div 2)}" fill="white" font-size="10" text-anchor="middle"><xsl:value-of select="concat($no-percentage,'%')"/></text>
</xsl:template>    
    
<xsl:template name="blank">
    <xsl:param name="counter"/>
    <xsl:param name="yes-bar-height"/>
    <xsl:param name="no-bar-height"/>
    <xsl:param name="blank-bar-height"/>
    <xsl:param name="blank-percentage"/>
            <rect x="{45*$counter+20}" y="{$chart-bottom - $yes-bar-height - $no-bar-height - $blank-bar-height}" fill="darkgrey" width="{$bar-width}" height="{$blank-bar-height}"/>
            <text x="{45*$counter+35}" y="{$chart-bottom - $yes-bar-height - $no-bar-height - ($blank-bar-height div 2)}" fill="white" font-size="10" text-anchor="middle"><xsl:value-of select="concat($blank-percentage,'%')"/></text>
</xsl:template>    

<xsl:template match="surveydata">
    <xsl:apply-templates/>
    </xsl:template>
    
<xsl:template match="comments"/>

</xsl:transform>

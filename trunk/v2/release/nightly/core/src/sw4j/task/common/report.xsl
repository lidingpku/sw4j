<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ***************************************************  -->
<!--	 		template: root					   -->
<!-- ***************************************************  -->

<xsl:template match="Sw4jResult">

<!--	===========  HTML HEADER =========== -->
<html>
<head>
	<title>	TW OWL instance data evaluation 
		<xsl:if test="//hasReport/Sw4jReport/file_or_url">
			- checking <xsl:value-of select="//hasReport/Sw4jReport/file_or_url"/>
		</xsl:if >
	</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="http://tw.rpi.edu/base.css" rel="stylesheet" type="text/css" />
<!--  TODO: will add more features via javascript
<script type="text/javascript" src="http://onto.rpi.edu/oie/report.js"></script>
-->

<style  type="text/css"> 
body.oie{
}


div.oie_section{
	margin: 0.5em;
}


div.oie_section_title{
	background: #DDE;
	text-align: center;
	color: #005A9C;
	font: 120% sans-serif ;
}

div.oie_section_content table{
text-align: left;
/*font-family: Verdana, Geneva, Arial, Helvetica, sans-serif ;
*/
font-weight: normal;
background-color: #EEE;
border: 0px;
border-collapse: collapse;
border-spacing: 0px;
font: 80% ;
}

div.oie_section_content td{
background-color: #DDD;
color: #000;
/*
padding: 4px;
*/
text-align: left;
border: 1px #fff solid;
}


div.oie_section_content td.header{
background-color: #AAA;
/*
color: #fff;
padding: 4px;
*/
text-align: left;
border-bottom: 2px #fff solid;
font-weight: bold;
}

div.oie_section_content td.has_issue{
color: #000;
}

div.oie_section_content td.has_no_issue{
color: #fff;
}


div.oie_report{
/*	border: 1 solid;
*/
	margin: 0.5em 1em;	
}


div.oie_report_title {
	margin-bottom: 0.5em;	
	color: #005A9C;
	font: bold 100% sans-serif ;
}

div.oie_report_entry {
/*	border: 1 solid;
	display: block;
	padding: 0.5em 1em;	
*/	
	margin: 0.5em 1em;
	background: #EEE;
}

div.oie_entry_abstract {
	font:   100%  sans-serif
}
div.oie_entry_detail {
	font: italic 100% times
}
</style>

</head>

<!--	===========  HTML HEADER =========== -->
<body class="oie">

<!--	+++++++  banner +++++++ -->
<div class="tw_banner">
	<h1>
		<a href="http://tw.rpi.edu/"><img class="tw_banner" border ="0" alt="W3C" id="logo" src="http://tw.rpi.edu/images/tw-logo-64.png" /></a>
		<span>TW OWL Instance Data Evaluation Result</span>
	</h1>
</div>


<!--	+++++++  section 1 summary +++++++ -->
<div id ="sec_1_summary" class="report_section">

	<div class="oie_section_title">Summary </div>

	<div class="oie_section_content" align="center">
		<p>Your OWL instance data 
			<xsl:if test="//hasReport/Sw4jReport/file_or_url">
				(<a href="{//hasReport/Sw4jReport/file_or_url}"> <xsl:value-of select="//hasReport/Sw4jReport/file_or_url"/></a>)
			</xsl:if >
			<b><xsl:value-of select="result"/></b> the following evaluation options.</p>

	<table border = "1">
		<tr>
			<td class="header">  Evaluation Options </td>		
			<td class="header"> <img src="http://tw.rpi.edu/images/misc/FATAL.png" /> FATAL ERROR(s) </td>		
			<td class="header"> <img src="http://tw.rpi.edu/images/misc/ERROR.png" /> ERROR(s) </td>		
			<td class="header"> <img src="http://tw.rpi.edu/images/misc/WARNING.png" /> WARNING(s) </td>		
		</tr>

		<xsl:for-each select="hasReport/Sw4jReport">
			<tr>
				<td><xsl:value-of select="report_title"/></td>
				<xsl:choose>
					<xsl:when test="count(hasMessage/Sw4jMessage[state='FATAL'])>0" >
					<td class="has_issue">
						<xsl:value-of select="count(hasMessage/Sw4jMessage[state='FATAL'])" />
					</td>
					</xsl:when>
					<xsl:otherwise>
					<td  class="has_no_issue"> 0 </td>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="count(hasMessage/Sw4jMessage[state='ERROR'])>0" >
					<td class="has_issue">
						<xsl:value-of select="count(hasMessage/Sw4jMessage[state='ERROR'])" />
					</td>
					</xsl:when>
					<xsl:otherwise>
					<td  class="has_no_issue"> 0 </td>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="count(hasMessage/Sw4jMessage[state='WARNING'])>0" >
					<td class="has_issue">
						<xsl:value-of select="count(hasMessage/Sw4jMessage[state='WARNING'])" />
					</td>
					</xsl:when>
					<xsl:otherwise>
					<td  class="has_no_issue"> 0 </td>
					</xsl:otherwise>
				</xsl:choose>
<!--
				<td  class="ERROR">
					<xsl:value-of select="count(hasMessage/Sw4jMessage[state='ERROR'])" />
				</td>
				<td  class="WARNING">
					<xsl:value-of select="count(hasMessage/Sw4jMessage[state='WARNING'])" />
				</td>
-->
			</tr>
		</xsl:for-each>
	</table>
</div>
</div>

<!--	+++++++  section 2 details +++++++ -->
<div id ="sec_2_details"  class="oie_section">

	<div class="oie_section_title">Details</div>

	<div class="oie_section_content"><xsl:apply-templates select="hasReport/Sw4jReport"/></div >
	
</div>

<!--	+++++++  section 3 raw rdf data +++++++ -->
<!--
<div id ="sec_3_raw_rdf_text"  class="oie_section">

	<div class="oie_section_title">Raw RDF text</div>

	<div class="oie_section_content">
		<xsl:if test="count(//hasRawLine/OieRawLine/*)>0">
			<ol>
			<xsl:for-each select="//hasRawLine/OieRawLine/*">
				<li><xsl:value-of select="."/></li> 
			</xsl:for-each>
			</ol>
		</xsl:if>
-->
<!--
		<xsl:value-of select="//hasReport/Sw4jReport/raw_content" />
		<xsl:value-of select="hasReport/Sw4jReport/raw_content" />
		<xsl:value-of select="//hasRawLine/OieRawLine" />
		<xsl:value-of select="//hasRawLine/OieRawLine/L01" />
		

		<xsl:for-each select="hasRawLine/OieRawLine">
			<xsl:value-of select="line_number"/>: <xsl:value-of select="line_content"/> 
		</xsl:for-each>
-->	
<!--
	</div >
	
</div>
-->



</body>
</html>
</xsl:template>


<!-- ***************************************************  -->
<!--	 	template: for individual report	  			  -->
<!-- ***************************************************  -->

<xsl:template match="hasReport/Sw4jReport">
	<xsl:if test="count(hasMessage)>0">
		<div class="oie_report">
			<div class="oie_report_title" title="{report_description}"> <xsl:value-of select="report_title"/> </div>

			<xsl:apply-templates select="hasMessage/Sw4jMessage"/>
		</div>
	</xsl:if>
</xsl:template>


<!-- ***************************************************  -->
<!--	 	template: for individual report	entry		  -->
<!-- ***************************************************  -->

<xsl:template match="hasMessage/Sw4jMessage">
	<div class="oie_report_entry">

		<div class="state"> 
			<xsl:choose>
				<xsl:when test="state='FATAL'">  <img src="http://tw.rpi.edu/images/misc/fatal.png" />    </xsl:when>	  
				<xsl:when test="state='WARNING'">  <img src="http://tw.rpi.edu/images/misc/warning.png" />    </xsl:when>	  
				<xsl:when test="state='ERROR'">  <img src="http://tw.rpi.edu/images/misc/error.png" />    </xsl:when>	  
			</xsl:choose>
			<xsl:text>   </xsl:text> 
			<xsl:value-of select="summary"/>
			( <xsl:value-of select="creator"/> )
		</div>
		 
		<xsl:if test="details">
			<div class="oie_entry_detail"> <xsl:value-of select="details"/> </div>
		</xsl:if>
	</div> 
</xsl:template>

</xsl:stylesheet>
<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ***************************************************  -->
<!--	 		template: root					   -->
<!-- ***************************************************  -->

<xsl:template match="OieResult">

<!--	===========  HTML HEADER =========== -->

<head>
	<title>	TW OWL instance data evaluation 
		<xsl:if test="//hasReport/OieReport/file_or_url">
			- checking <xsl:value-of select="//hasReport/OieReport/file_or_url"/>
		</xsl:if >
	</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="http://tw.rpi.edu/base.css" rel="stylesheet" type="text/css" />
	<link href="http://onto.rpi.edu/demo/oie/report.css" rel="stylesheet" type="text/css" /> 
<!--  TODO: will add more features via javascript
<script type="text/javascript" src="http://onto.rpi.edu/oie/report.js"></script>
-->
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
			<xsl:if test="//hasReport/OieReport/file_or_url">
				(<a href="{//hasReport/OieReport/file_or_url}"> <xsl:value-of select="//hasReport/OieReport/file_or_url"/></a>)
			</xsl:if >
			<b><xsl:value-of select="result"/></b> the following evaluation options.</p>

	<table border = "1">
		<tr>
			<td class="header">  Evaluation Options </td>		
			<td class="header"> <img src="http://tw.rpi.edu/images/misc/fatal.png" /> fatal error(s) </td>		
			<td class="header"> <img src="http://tw.rpi.edu/images/misc/error.png" /> error(s) </td>		
			<td class="header"> <img src="http://tw.rpi.edu/images/misc/warn.png" /> warning(s) </td>		
		</tr>

		<xsl:for-each select="hasReport/OieReport">
			<tr>
				<td><xsl:value-of select="report_title"/></td>
				<xsl:choose>
					<xsl:when test="count(hasEntry/OieEntry[level='fatal'])>0" >
					<td class="has_issue">
						<xsl:value-of select="count(hasEntry/OieEntry[level='fatal'])" />
					</td>
					</xsl:when>
					<xsl:otherwise>
					<td  class="has_no_issue"> 0 </td>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="count(hasEntry/OieEntry[level='error'])>0" >
					<td class="has_issue">
						<xsl:value-of select="count(hasEntry/OieEntry[level='error'])" />
					</td>
					</xsl:when>
					<xsl:otherwise>
					<td  class="has_no_issue"> 0 </td>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="count(hasEntry/OieEntry[level='warn'])>0" >
					<td class="has_issue">
						<xsl:value-of select="count(hasEntry/OieEntry[level='warn'])" />
					</td>
					</xsl:when>
					<xsl:otherwise>
					<td  class="has_no_issue"> 0 </td>
					</xsl:otherwise>
				</xsl:choose>
<!--
				<td  class="error">
					<xsl:value-of select="count(hasEntry/OieEntry[level='error'])" />
				</td>
				<td  class="warn">
					<xsl:value-of select="count(hasEntry/OieEntry[level='warn'])" />
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

	<div class="oie_section_content"><xsl:apply-templates select="hasReport/OieReport"/></div >
	
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
		<xsl:value-of select="//hasReport/OieReport/raw_content" />
		<xsl:value-of select="hasReport/OieReport/raw_content" />
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

</xsl:template>


<!-- ***************************************************  -->
<!--	 	template: for individual report	  			  -->
<!-- ***************************************************  -->

<xsl:template match="hasReport/OieReport">
	<xsl:if test="count(hasEntry)>0">
		<div class="oie_report">
			<div class="oie_report_title" title="{report_description}"> <xsl:value-of select="report_title"/> </div>

			<xsl:apply-templates select="hasEntry/OieEntry"/>
		</div>
	</xsl:if>
</xsl:template>


<!-- ***************************************************  -->
<!--	 	template: for individual report	entry		  -->
<!-- ***************************************************  -->

<xsl:template match="hasEntry/OieEntry">
	<div class="oie_report_entry">

		<div class="oie_entry_abstract"> 
			<xsl:choose>
				<xsl:when test="level='fatal'">  <img src="http://tw.rpi.edu/images/misc/fatal.png" />    </xsl:when>	  
				<xsl:when test="level='warn'">  <img src="http://tw.rpi.edu/images/misc/warn.png" />    </xsl:when>	  
				<xsl:when test="level='error'">  <img src="http://tw.rpi.edu/images/misc/error.png" />    </xsl:when>	  
			</xsl:choose>
			<xsl:text>   </xsl:text> 
			<xsl:value-of select="message"/>
		</div>
		 
		<xsl:if test="details">
			<div class="oie_entry_detail"> <xsl:value-of select="details"/> </div>
		</xsl:if>
	</div> 
</xsl:template>

</xsl:stylesheet>
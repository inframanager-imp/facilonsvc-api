<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Account Opening</title>

<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.9.3/html2pdf.bundle.min.js"></script>

<style>

body{
    margin:0;
    background:#e9edf1;
    font-family:"Segoe UI",Arial,sans-serif;
    font-size:15px;
}

/* SCREEN WRAPPER */
.screen-wrapper{
    padding:40px 0;
}

/* A4 WRAPPER (PDF TARGET) */
#a4-wrapper{
    width:210mm;
    margin:auto;
    background:white;
}

/* PAGE CONTENT */
.page{
    padding:20mm;
}

/* HEADER */

.top-bar{
    height:6px;
    background:#2c3e50;
    margin:-20mm -20mm 25px -20mm;
}

.title{
    text-align:center;
    font-size:24px;
    font-weight:600;
}

/* GRID */

.grid{
    display:flex;
    justify-content:space-between;
    margin-top:25px;
    font-size:14px;
}

.right{text-align:right}

/* SECTIONS */

.section{margin-top:25px}

.subject{
    background:#f4f6f8;
    padding:12px;
    text-align:center;
    font-weight:600;
    border-left:4px solid #2c3e50;
    margin:25px 0;
}

/* TABLE */

table{
    width:100%;
    border-collapse:collapse;
    margin-top:20px;
    font-size:13px;
}

th{
    background:#2c3e50;
    color:white;
    padding:10px;
}

td{
    border:1px solid #ddd;
    padding:9px;
}

/* FOOTER */

.signature{
    margin-top:60px;
    border-top:1px solid #ccc;
    padding-top:15px;
    width:250px;
}

.footer{margin-top:40px;font-size:13px}

</style>
</head>

<body>

<div class="screen-wrapper">

<div id="a4-wrapper">

<div class="page" id="content">

<div class="top-bar"></div>

<div class="title">
${userFirstName!""} ${userMiddleName!""} ${userLastName!""}
</div>

<div class="grid">

<div>
<strong>${serviceProviderName!""}</strong><br>
Mumbai<br>
${serviceProviderEmail!""}<br>
${serviceProviderPhone!""}
</div>

<div class="right">
${userAddressLine1!""}<br>
<#if userAddressLine2?has_content>${userAddressLine2}<br></#if>
${userCity!""}<br>
<#if userMobile?has_content>Mobile: +91 ${userMobile}<br></#if>
Email: ${userEmail!""}
</div>

</div>

<div style="text-align:center;margin-top:12px"><strong>Through Courier</strong></div>

<p>Dear Sir / Madam,</p>

<div class="subject">
Account Opening Request – ${productName!""}
</div>

<p>
We hereby submit the enclosed documents duly executed and authenticated for opening the account in the name of
<strong>${userFirstName?upper_case!""} ${userMiddleName?upper_case!""} ${userLastName?upper_case!""}</strong>.
</p>

<table>

<thead>
<tr>
<th>Sr</th>
<th>Document</th>
<th>Authentication</th>
<th>Copies</th>
<th>Status</th>
</tr>
</thead>

<tbody>
<#if documents?has_content>
<#list documents as document>
<tr>
<td>${document?index + 1}</td>
<td>${document.documentType!""}</td>
<td>
<#if document.uploadType == 2>
Signed &amp; Scanned
<#else>
Self Attested
</#if>
</td>
<td>1</td>
<td>✓</td>
</tr>
</#list>
<#else>
<tr>
<td colspan="5" style="text-align:center">No documents found</td>
</tr>
</#if>
</tbody>

</table>

<div class="footer">

<p>Thanking you.</p>

<p>Yours sincerely,</p>

<div class="signature">
<strong>
${userFirstName?upper_case!""} ${userMiddleName?upper_case!""} ${userLastName?upper_case!""}
</strong>
</div>

</div>

</div>
</div>

</div>

<script>

window.onload=function(){

html2pdf().set({
margin:0,
filename:'investor-document-checklist.pdf',
html2canvas:{scale:2},
jsPDF:{unit:'mm',format:'a4',orientation:'portrait'}
}).from(document.getElementById('a4-wrapper')).save();

}

</script>

</body>
</html>

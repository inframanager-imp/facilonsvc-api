<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Personal Information - Print Preview</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
    <style>
        * {
            box-sizing: border-box;
        }
        
        body {
            text-transform: uppercase !important;
            font-family: 'Times New Roman', Times, serif;
            font-size: 12pt;
            line-height: 1.7;
            margin: 0;
            padding: 0;
            background-color: #f0f0f0;
            color: #000;
        }
        
        .container {
            max-width: 210mm;
            margin: 0 auto;
            padding: 28mm 22mm 35mm 22mm;
            background: white;
            min-height: 297mm;
            box-shadow: 0 8px 30px rgba(0,0,0,0.12);
        }
        
        .header {
            text-align: center;
            margin-bottom: 50px;
            padding-bottom: 35px;
            border-bottom: 5px double #be1717;
            position: relative;
        }
        
        .logo {
            max-width: 190px;
            height: auto;
            margin: 0 auto 30px auto;
            display: block;
            padding: 12px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
            border-bottom: 4px solid #be1717;
        }
        
        .document-title {
            font-size: 24pt;
            font-weight: bold;
            color: #1a1a1a;
            margin: 0;
            letter-spacing: 3px;
            padding-bottom: 12px;
            border-bottom: 4px solid #be1717;
            display: inline-block;
        }
        
        .section {
            margin-bottom: 45px;
            page-break-inside: avoid;
        }
        
        .section-title {
            font-size: 18pt;
            font-weight: bold;
            color: #be1717;
            text-align: center;
            margin: 50px 0 35px 0;
            padding: 18px 0;
            letter-spacing: 1.5px;
            position: relative;
        }
        
        .section-title::after {
            content: '';
            display: block;
            width: 140px;
            height: 5px;
            background: #be1717;
            margin: 18px auto 0;
            border-radius: 4px;
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px 30px;
            margin-bottom: 20px;
        }
        
        .info-item {
            display: flex;
            align-items: flex-start;
            padding: 8px 0;
            border-bottom: 1px dotted #ccc;
        }
        
        .info-label {
            font-weight: bold;
            min-width: 120px;
            margin-right: 15px;
            color: #333;
        }
        
        .info-value {
            flex: 1;
            color: #000;
            word-break: break-word;
        }
        
        .footer-note {
            text-align: center;
            font-style: italic;
            margin-top: 40px;
            padding: 20px;
            border: 1px dashed #999;
            background-color: #f9f9f9;
        }
        
        .signature-section {
            margin-top: 50px;
            display: flex;
            justify-content: space-between;
        }
        
        .signature-box {
            text-align: center;
            width: 200px;
        }
        
        .signature-line {
            border-top: 1px solid #333;
            margin-top: 40px;
            padding-top: 5px;
            font-size: 10pt;
        }
        
        .pdf-button-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            background: rgba(255, 255, 255, 0.97);
            padding: 18px 20px;
            border-radius: 14px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
            backdrop-filter: blur(12px);
            border: 1px solid #d0d0d0;
            display: flex;
            flex-direction: column;
            gap: 14px;
            align-items: flex-end;
        }
        
        .pdf-button, .pdf-button1 {
            background: linear-gradient(135deg, #be1717 0%, #a01515 100%);
            color: white;
            border: none;
            padding: 12px 26px;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            box-shadow: 0 6px 18px rgba(190, 23, 23, 0.35);
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            min-width: 190px;
            text-decoration: none;
        }
        
        .pdf-button:hover, .pdf-button1:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 25px rgba(190, 23, 23, 0.45);
        }
        
        .pdf-icon {
            width: 19px;
            height: 19px;
        }
        
        .spinner {
            width: 17px;
            height: 17px;
            border: 2.5px solid #ffffff;
            border-top: 2.5px solid transparent;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .pdf-icon {
            width: 16px;
            height: 16px;
        }
        
        .spinner {
            width: 16px;
            height: 16px;
            border: 2px solid #ffffff;
            border-top: 2px solid transparent;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        @media print {
            @page {
                size: A4;
                margin: 10mm !important;
            }
            body {
                margin: 0 !important;
                padding: 0 !important;
            }
            .container {
                width: 210mm !important;
                min-height: auto !important;
                padding: 10mm !important;
                margin: 0 !important;
                box-shadow: none !important;
            }
            .info-item {
                break-inside: avoid !important;
                page-break-inside: avoid !important;
            }
            .section, h2.section-title, h4 {
                break-before: auto !important;
                break-after: auto !important;
                break-inside: avoid !important;
            }
            .section + .section {
                break-before: page !important;
            }
            .pdf-button-container {
                display: none;
            }
        }
        
        @media screen {
            body {
                background-color: #e5e5e5;
                padding: 20px 0;
            }
            
            .container {
                box-shadow: 0 0 20px rgba(0,0,0,0.1);
            }
        }
    </style>
</head>
<body>
<div class="pdf-button-container">
    <button class="pdf-button" onclick="downloadPDF()" id="downloadButton">
        <svg class="pdf-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
        </svg>
        Download PDF
    </button>
    <button class="pdf-button" onclick="submitProfile()" id="submitButton">
        <svg class="pdf-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9,16.17L4.83,12L3.41,13.41L9,19L21,7L19.59,5.59L9,16.17Z"/>
        </svg>
        Submit Profile
    </button>
</div>

<div class="container" id="content">
    <!-- Header Section -->
    <div class="header">
        <img src="https://www.facilonservices.com/images/logo.png" alt="Company Logo" class="logo" />
        <h1 class="document-title">Investor Details</h1>
    </div>
    
    <!-- Personal Information Section -->
    <h2 class="section-title">Personal Details</h2>
    <div class="info-grid">
        <div class="info-item">
            <span class="info-label">Name:</span>
            <span class="info-value">${personalInfo.title!''} ${personalInfo.investorFirstName!''} ${personalInfo.investorMiddleName!''} ${personalInfo.investorLastName!''}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Gender:</span>
            <span class="info-value">${personalInfo.investorGender!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Marital Status:</span>
            <span class="info-value">${personalInfo.maritalStatus!'N/A'}</span>
        </div>
        
        <#if personalInfo.maidenName?has_content>
        <div class="info-item">
            <span class="info-label">Maiden Name:</span>
            <span class="info-value">${personalInfo.maidenTitle!''} ${personalInfo.maidenName!''} ${personalInfo.maidenMiddleName!''} ${personalInfo.maidenLastName!''}</span>
        </div>
        </#if>
        
        <div class="info-item">
            <span class="info-label">Date of Birth:</span>
            <span class="info-value">${personalInfo.userDob!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">City of Birth:</span>
            <span class="info-value">${personalInfo.cityOfDob!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Country of Birth:</span>
            <span class="info-value">${personalInfo.countryDob!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Citizenship:</span>
            <span class="info-value">${personalInfo.citizenship!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Country of Residence:</span>
            <span class="info-value">${personalInfo.countryOfResidence!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">PAN No:</span>
            <span class="info-value">${personalInfo.userPanNo!'N/A'}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Father's Name:</span>
            <span class="info-value">${personalInfo.fatherNameTitle!''} ${personalInfo.fathersFirstName!''} ${personalInfo.fathersMiddleName!''} ${personalInfo.fathersLastName!''}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Mother's Name:</span>
            <span class="info-value">${personalInfo.motherNameTitle!''} ${personalInfo.motherFirstName!''} ${personalInfo.motherMiddleName!''} ${personalInfo.motherLastName!''}</span>
        </div>
    </div>
    
    <div class="section">
        <!-- Bank Details Section -->
        <h2 class="section-title">Bank Details</h2>
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Type Of Account:</span>
                <span class="info-value">${bankDetails.accountType!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Do You have PIS Approval?:</span>
                <span class="info-value">${bankDetails.rbiApproval!'N/A'}</span>
            </div>
            
            <#if bankDetails.rbiApprovalOrderNumber?has_content>
            <div class="info-item">
                <span class="info-label">PIS Approval No:</span>
                <span class="info-value">${bankDetails.rbiApprovalOrderNumber}</span>
            </div>
            </#if>
            
            <#if bankDetails.rbiApprovalDate?has_content>
            <div class="info-item">
                <span class="info-label">PIS Approval Date:</span>
                <span class="info-value">${bankDetails.rbiApprovalDate}</span>
            </div>
            </#if>
            
            <div class="info-item">
                <span class="info-label">Beneficiary Name:</span>
                <span class="info-value">${bankDetails.accountHolderName!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Bank Name:</span>
                <span class="info-value">${bankDetails.bankName!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Branch Name:</span>
                <span class="info-value">${bankDetails.branchName!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Bank Address:</span>
                <span class="info-value">${bankDetails.bankAddress!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Account No:</span>
                <span class="info-value">${bankDetails.accountNumber!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">IFSC Code:</span>
                <span class="info-value">${bankDetails.ifscCode!'N/A'}</span>
            </div>
        </div>
        
        <!-- Passport Section -->
        <h2 class="section-title">Passport</h2>
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Nationality:</span>
                <span class="info-value">${passportDetails.nationality!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Passport No:</span>
                <span class="info-value">${passportDetails.passportNumber!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Date of Issue:</span>
                <span class="info-value">${passportDetails.dateOfIssue!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Place of Issue:</span>
                <span class="info-value">${passportDetails.placeOfIssue!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Valid Upto:</span>
                <span class="info-value">${passportDetails.validUpto!'N/A'}</span>
            </div>
            
            <#if passportDetails.becomingNonResidentDate?has_content>
            <div class="info-item">
                <span class="info-label">Becoming Non Resident Date:</span>
                <span class="info-value">${passportDetails.becomingNonResidentDate}</span>
            </div>
            </#if>
            
            <#if passportDetails.yearsAbroad?has_content>
            <div class="info-item">
                <span class="info-label">No of years in Abroad:</span>
                <span class="info-value">${passportDetails.yearsAbroad} Years</span>
            </div>
            </#if>
        </div>
        
        <!-- Residential Status Section -->
        <h2 class="section-title">Residential Status</h2>
        <div class="info-grid">
            <#if residentialStatus.ociCardNo?has_content>
            <div class="info-item">
                <span class="info-label">OCI Card No:</span>
                <span class="info-value">${residentialStatus.ociCardNo}</span>
            </div>
            </#if>
            
            <#if residentialStatus.ociIssueDate?has_content>
            <div class="info-item">
                <span class="info-label">Issue Date:</span>
                <span class="info-value">${residentialStatus.ociIssueDate}</span>
            </div>
            </#if>
            
            <#if residentialStatus.ociValidUpto?has_content>
            <div class="info-item">
                <span class="info-label">Valid Upto:</span>
                <span class="info-value">${residentialStatus.ociValidUpto}</span>
            </div>
            </#if>
            
            <#if residentialStatus.typeOfProof?has_content>
            <div class="info-item">
                <span class="info-label">Type Of Proof:</span>
                <span class="info-value">${residentialStatus.typeOfProof}</span>
            </div>
            </#if>
            
            <#if residentialStatus.visaType?has_content>
            <div class="info-item">
                <span class="info-label">Visa Type:</span>
                <span class="info-value">${residentialStatus.visaType}</span>
            </div>
            </#if>
            
            <#if residentialStatus.visaNumber?has_content>
            <div class="info-item">
                <span class="info-label">Visa Number:</span>
                <span class="info-value">${residentialStatus.visaNumber}</span>
            </div>
            </#if>
            
            <#if residentialStatus.visaIssuerDate?has_content>
            <div class="info-item">
                <span class="info-label">Visa Issuer Date:</span>
                <span class="info-value">${residentialStatus.visaIssuerDate}</span>
            </div>
            </#if>
            
            <#if residentialStatus.visaExpiryDate?has_content>
            <div class="info-item">
                <span class="info-label">Visa Expiry Date:</span>
                <span class="info-value">${residentialStatus.visaExpiryDate}</span>
            </div>
            </#if>
        </div>
    </div>
    
    <div class="section">
        <!-- Tax Information Section -->
        <h2 class="section-title">Tax Information</h2>
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Country:</span>
                <span class="info-value">${taxInfo.currentCountryResidenceForTax!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Identification No:</span>
                <span class="info-value">${taxInfo.taxIdentificationNumber!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Identification No Type:</span>
                <span class="info-value">${taxInfo.taxIdentificationNumberType!'N/A'}</span>
            </div>
            
            <#if taxInfo.taxResidencyCertificateNo?has_content>
            <div class="info-item">
                <span class="info-label">Certificate No:</span>
                <span class="info-value">${taxInfo.taxResidencyCertificateNo}</span>
            </div>
            </#if>
            
            <#if taxInfo.taxResidencyCertificateDate?has_content>
            <div class="info-item">
                <span class="info-label">Certificate Date:</span>
                <span class="info-value">${taxInfo.taxResidencyCertificateDate}</span>
            </div>
            </#if>
            
            <div class="info-item">
                <span class="info-label">Are you US Person under FATCA?:</span>
                <span class="info-value">${taxInfo.usPersonFatca!'N/A'}</span>
            </div>
        </div>
        
        <!-- Contact Details Section -->
        <h2 class="section-title">Contact Details</h2>
        <span><u>Residential</u></span><br />
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Email ID:</span>
                <span class="info-value">${contactDetails.email!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Phone No:</span>
                <span class="info-value">${contactDetails.mobileNo!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Address Line 1:</span>
                <span class="info-value">${contactDetails.addressLine1!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Address Line 2:</span>
                <span class="info-value">${contactDetails.addressLine2!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Address Line 3:</span>
                <span class="info-value">${contactDetails.addressLine3!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">City:</span>
                <span class="info-value">${contactDetails.userCity!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">State:</span>
                <span class="info-value">${contactDetails.userState!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Country:</span>
                <span class="info-value">${contactDetails.userCountry!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Postal/Zip code:</span>
                <span class="info-value">${contactDetails.userZipCode!'N/A'}</span>
            </div>
        </div>
        
        <#if contactDetails.corrAddressLine1?has_content>
        <span><u>Correspondence</u></span><br />
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Address Line 1:</span>
                <span class="info-value">${contactDetails.corrAddressLine1}</span>
            </div>
            
            <#if contactDetails.corrAddressLine2?has_content>
            <div class="info-item">
                <span class="info-label">Address Line 2:</span>
                <span class="info-value">${contactDetails.corrAddressLine2}</span>
            </div>
            </#if>
            
            <#if contactDetails.corrAddressLine3?has_content>
            <div class="info-item">
                <span class="info-label">Address Line 3:</span>
                <span class="info-value">${contactDetails.corrAddressLine3}</span>
            </div>
            </#if>
            
            <div class="info-item">
                <span class="info-label">City:</span>
                <span class="info-value">${contactDetails.corrUserCity!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">State:</span>
                <span class="info-value">${contactDetails.corrUserState!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Country:</span>
                <span class="info-value">${contactDetails.corrUserCountry!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Postal/Zip code:</span>
                <span class="info-value">${contactDetails.corrUserZipCode!'N/A'}</span>
            </div>
        </div>
        </#if>
        
        <!-- Nomination Details Section -->
        <h2 class="section-title">Nomination Details</h2>
        <#list nominations as nomination>
        <h3>Nominee ${nomination?index + 1}</h3>
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Nominee Name:</span>
                <span class="info-value">${nomination.nomineeName!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Relationship:</span>
                <span class="info-value">${nomination.relationship!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Percentage Share:</span>
                <span class="info-value">${nomination.percentageShare!'N/A'}%</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Mobile No:</span>
                <span class="info-value">${nomination.mobileNo!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">PAN No:</span>
                <span class="info-value">${nomination.panNo!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Date of Birth:</span>
                <span class="info-value">${nomination.dateOfBirth!'N/A'}</span>
            </div>
            
            <#if nomination.guardianName?has_content>
            <div class="info-item">
                <span class="info-label">Guardian Name:</span>
                <span class="info-value">${nomination.guardianName}</span>
            </div>
            </#if>
            
            <#if nomination.guardianDocType?has_content>
            <div class="info-item">
                <span class="info-label">Guardian Document Type:</span>
                <span class="info-value">${nomination.guardianDocType}</span>
            </div>
            </#if>
            
            <#if nomination.guardianDocNo?has_content>
            <div class="info-item">
                <span class="info-label">Guardian Document No:</span>
                <span class="info-value">${nomination.guardianDocNo}</span>
            </div>
            </#if>
        </div>
        </#list>
    </div>
    
    <div class="section">
        <!-- Risk Profile / Other Information Section -->
        <h2 class="section-title">Other Information</h2>
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Source of Funds:</span>
                <span class="info-value">${riskProfile.sourceOfFunds!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Source of Wealth:</span>
                <span class="info-value">${riskProfile.sourceOfWealth!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Education Qualification:</span>
                <span class="info-value">${riskProfile.educationalQualification!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Gross/ Monthly Income:</span>
                <span class="info-value">${riskProfile.grossIncome!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Net worth:</span>
                <span class="info-value">${riskProfile.netWorth!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Occupation:</span>
                <span class="info-value">${riskProfile.occupation!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Line of Business:</span>
                <span class="info-value">${riskProfile.lineOfBusiness!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Nature of Organization:</span>
                <span class="info-value">${riskProfile.natureOfOrganisation!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Politically Exposed:</span>
                <span class="info-value">${riskProfile.politicallyExposed!'NO'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Related to Politically Exposed:</span>
                <span class="info-value">${riskProfile.relatedToPoliticallyExposed!'NO'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Foreign Exchange/ Money Changer:</span>
                <span class="info-value">${riskProfile.foreignExchangeMoneyChanger!'NO'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Gaming/Gambling/Lottery:</span>
                <span class="info-value">${riskProfile.gamingGamblingLottery!'NO'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Money Lending/Pawning:</span>
                <span class="info-value">${riskProfile.moneyLendingPawning!'NO'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Any instance of violation or non-adherence to the securities laws, code of ethics / conduct, code of business rules:</span>
                <span class="info-value">${riskProfile.violationOfSecuritiesLaws!'NO'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Investment Experience(Years):</span>
                <span class="info-value">${riskProfile.investmentExperienceYears!'N/A'}</span>
            </div>
            
            <div class="info-item">
                <span class="info-label">Investment Experience In:</span>
                <span class="info-value">${riskProfile.investmentExperienceIn!'N/A'}</span>
            </div>
        </div>
    </div>
</div>

<script>
    function downloadPDF() {
        const button = document.getElementById('downloadButton');
        const originalContent = button.innerHTML;
        
        button.disabled = true;
        button.innerHTML = '<div class="spinner"></div> Generating...';
        
        const element = document.getElementById('content');
        
        const options = {
            margin: [0.5, 0.5, 0.5, 0.5],
            filename: 'Investor_Application_Status.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true, letterRendering: true, allowTaint: false },
            jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
        };
        
        html2pdf()
            .set(options)
            .from(element)
            .save()
            .then(() => {
                button.disabled = false;
                button.innerHTML = originalContent;
            })
            .catch((error) => {
                console.error('Error generating PDF:', error);
                button.disabled = false;
                button.innerHTML = originalContent;
                alert('Error generating PDF. Please try again.');
            });
    }
    
    function submitProfile() {
        if (confirm('Are you sure you want to submit your profile? This action cannot be undone.')) {
            // Close the preview window and redirect parent to profile page for submission
            if (window.opener) {
                window.opener.location.href = '/investor/profile';
                window.close();
            } else {
                // If not opened in new window, redirect to profile page
                window.location.href = '/investor/profile';
            }
        }
    }
</script>
</body>
</html>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Account Opening Kit - Individual</title>
    
    <style>
        @font-face {
            font-family: 'Arial Narrow';
            font-style: normal;
            font-weight: normal;
        }
        
        body {
            font-family: 'Arial Narrow', Arial, sans-serif;
            font-size: 12pt;
            margin: 0;
            padding: 0;
        }

        p {
            text-align: justify;
            font-weight: 700;
        }

        a {
            color: windowtext;
            text-decoration: none;
        }

        .WordSection1 {
            margin: 0;
            padding: 0;
            height: 100%;
            overflow: hidden;
        }

        .main-table-row tr {
            border: none;
        }

        .main-table-row td {
            border: none;
            padding: 5px;
        }

        .main-table-row th {
            border: none;
        }

        .header,
        .footer {
            width: 100%;
            position: fixed;
            background-color: #f58220;
            color: #fff;
            text-align: center;
            padding: 10px;
        }

        .header {
            height: 200px;
        }

        .footer {
            font-size: 15px;
        }

        .logo {
            margin-top: 180px;
            text-align: center;
        }

        .logo img {
            max-width: 100%;
            height: auto;
            width: auto;
            margin-top: 190px;
            margin-bottom: 170px;
        }

        .form-section {
            width: 80%;
            border: 2px solid #000;
            padding: 20px;
            margin: 20px auto;
            box-sizing: border-box;
        }

        .form-section p {
            margin: 10px 0;
            font-size: 16px;
        }

        table {
            border: none;
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            border: 1px solid #000000;
            padding: 8px;
            text-align: left;
        }

        .underline {
            text-decoration: underline;
            display: inline-block;
            text-align: left;
            line-height: 20px;
            text-transform: Capitalize;
            width: 100%;
        }

        .label {
            color: rgb(145, 145, 145);
            font-size: 13px;
        }

        th {
            background-color: #f2f2f2;
        }

        .instructions-header {
            font-family: "Trebuchet MS", sans-serif;
            color: black;
            background-color: #A9A9A9;
            padding: 5px;
            font-weight: bold;
            font-size: 11px;
        }

        .list-paragraph {
            margin-top: 5px;
            margin-left: 65px;
            text-align: justify;
        }

        .list-paragraph b {
            font-weight: bold;
        }

        .square-box {
            display: inline-block;
            width: 10px;
            height: 10px;
            border: 1px solid #000;
        }

        .small-square-box {
            width: 17px !important;
            height: 17px !important;
            border: 1px solid #1f1e1e;
            display: flex !important;
            justify-content: center !important;
            align-items: center !important;
            background-color: #f9f9f9 !important;
        }

        .d-flex {
            display: flex;
            align-items: center; 
            justify-content: flex-start;
        }

        .page-break {
            page-break-after: always;
        }

        .list-font {
            font-size: 11px;
        }

        .WordSection2 {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
        }

        .WordSection3 {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
        }

        .WordSection4 {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
        }

        .WordSection5 {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
        }

        td div {
            display: flex;
            gap: 5px;
            align-items: center;
        }

        .form-section {
            max-height: calc(100% - 220px);
            overflow: hidden;
        }

        .footer span {
            display: block;
            font-size: 14px;
            margin-top: 5px;
            font-weight: normal;
        }

        .logo-container {
            position: absolute;
            top: 0;
            right: 0;
        }

        .logo-container img {
            height: 50px;
            width: 200px;
        }

        .heading {
            margin-top: 6.95pt;
            margin-left: 45.35pt;
            text-indent: -19.85pt;
        }

        .body-text {
            margin-top: 6.3pt;
            margin-left: 45.35pt;
        }

        .body-text p {
            margin-bottom: 0;
        }

        .body-text span {
            font-weight: bold;
        }

        .new-table-border {
            border: none;
        }

        .new-table-border th {
            border: none;
        }

        .new-table-border tr {
            border: none;
        }

        .new-table-border td {
            border: none;
            padding: 5px;
        }

        .pdf-bordered {
            border-collapse: collapse;
            width: 100%;
            font-size: 11px;
        }

        .pdf-bordered td,
        .pdf-bordered th {
            border: 1px solid #000;
            padding: 6px;
        }

        .note-text {
            text-align: justify;
            font-size: 11px;
            font-weight: normal;
        }

        @page {
            size: A4;
            margin: 15mm;
        }
    </style>
</head>

<body>
    <!-- Cover Page with Client Details -->
    <div class="WordSection1">
        <!-- Header Section -->
        <div class="header"></div>

        <!-- Logo -->
        <div class="logo">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" />
        </div>

        <!-- Form Section -->
        <div class="form-section">
            <p>Name Of Client :
                <span style="display: inline-block; width: 50%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: left;">
                    ${personalInfo.investorFirstName!''} ${personalInfo.investorLastName!''}
                </span>
            </p>
            <p>Client Code :
                <span style="display: inline-block; width: 25%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: center;">
                    ${investor.uniqueCode!''}
                </span>
                &nbsp;&nbsp;
                Client ID :
                <span style="display: inline-block; width: 25%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: center;">
                    <!-- Client ID placeholder -->
                </span>
            </p>
            <p>Branch :
                <span style="display: inline-block; width: 50%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: left;">
                    <!-- Branch placeholder -->
                </span>
            </p>
        </div>

        <!-- Footer Section -->
        <div class="footer">
            Client Registration Form - INDIVIDUAL
            <span>(Equity + Demat + Commodity)</span>
            <span>THROUGH BUSINESS ASSOCIATES</span>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- Index / Table of Contents -->
    <div class="WordSection2">
        <div style="text-align: center; margin-top: 1.9pt; margin-right: 0in; margin-bottom: 2.3pt; margin-left: 2.85pt; font-weight: 600;">
            ACCOUNT OPENING KIT - INDIVIDUAL INDEX
        </div>
        <br />

        <table class="table table-bordered" style="border: 1px solid #000000; font-size: 11px;">
            <thead>
                <tr>
                    <th rowspan="2" style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">Sr. No.</th>
                    <th rowspan="2" style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">Name of the Document</th>
                    <th rowspan="2" style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">Brief Significance of the Document</th>
                    <th rowspan="2" style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">Part</th>
                    <th colspan="2" style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">Page No.</th>
                </tr>
                <tr>
                    <th style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">From</th>
                    <th style="background: #D1D3D4; text-align: center;border: 1px solid #000000;font-size: 11px;">To</th>
                </tr>
            </thead>

            <tbody>
                <tr style="border: 1px solid black !important; font-size: 11px;">
                    <td colspan="6" style="text-align: left; border: 1px solid black !important;">
                        <strong>MANDATORY DOCUMENTS AS PRESCRIBED BY SEBI / EXCHANGES / DEPOSITORY</strong>
                    </td>
                </tr>
                
                <tr style="border: 1px solid #000000; font-size: 11px;">
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;" rowspan="2">1</td>
                    <td style="text-align: left; border: 1px solid #000000; font-size: 11px;" rowspan="2">Account Opening Form</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        A. KYC form - Document captures the basic information about the constituent and In-Person Verification details and instructions.
                    </td>
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;">A</td>
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;">1</td>
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;">8</td>
                </tr>
                
                <tr style="border: 1px solid #000000; font-size: 11px;">
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        B. Document captures the additional information about the constituent relevant to trading and demat account.
                    </td>
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;">A</td>
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;">9</td>
                    <td style="text-align: center; border: 1px solid #000000; font-size: 11px;">13</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000; font-size: 11px;">2</td>
                    <td style="border: 1px solid #000000;">Tariff Schedule - Equity</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing the rate/amount of brokerage and other charges levied on the client for trading on the stock exchange(s).</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">3</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Schedule of DP Charges and Option for DIS Booklet</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Charges for Depository Services / Demat charges and option for issuance of DIS booklet.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">16</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">17</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">4</td>
                    <td style="border: 1px solid #000000; font-size: 11px;">Acknowledgement letter</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Acknowledgement indicating receipt of documents by client.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">18</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">5</td>
                    <td style="border: 1px solid #000000;font-size: 11px;">FATCA/CRS Declaration Form</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Account opening form supplement</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">24</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">26</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">6</td>
                    <td style="border: 1px solid #000000;font-size: 11px;">Consent &amp; MITC</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Consent for usage of Aadhaar Number &amp; Most Important Terms and Conditions (MITC)</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">27</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">28</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">7</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Rights and Obligations</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing the rights and obligations of the account holder.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">29</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">31</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">8</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Risk Disclosure Document (RDD)</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing risks associated with dealing in the securities market.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">11</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">13</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">9</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Guidance Note</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing do's and don'ts for trading on exchange, for the education of the investors.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">15</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">10</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Policies and Procedures</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document describing significant policies and procedures of the service provider.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">16</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">18</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">11</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Rights and Obligations of DP</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Rights and Obligations of Beneficial Owner and Depository Participant as prescribed by SEBI and Depositories.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">34</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">35</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">12</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Intimation of Money Laundering</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">This document is to make the client aware of Anti Money Laundering (AML) provisions.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">36</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">36</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">13</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Risk Disclosure Document, Rights and Obligations and Do's &amp; Don'ts (Commodities)</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing risks associated with dealing in Commodity Market, Rights and Obligations and Do's &amp; Don'ts for trading on Commodity Exchange for the education of the investor.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">37</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">49</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Investor Charter - Stock Brokers</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Annexure-A.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">50</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">53</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">15</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Investor Charter - DP</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Investor Charter by Depository Participants.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">54</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">59</td>
                </tr>

                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td colspan="6" style="text-align: center; border: 1px solid #000000;font-size: 11px;">
                        <strong>VOLUNTARY DOCUMENTS AS PROVIDED BY THE STOCK BROKER / DEPOSITORY</strong>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">16</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Letter of Appointment</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        This document allows the client to give authority to another person for placing / giving / executing orders on his / her behalf.
                    </td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                </tr>
                <tr>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">17</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Authorization for running account / request letter</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        Letter of authority / request to Service Provider
                    </td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">20</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">20</td>
                </tr>
                <tr>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">18</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Other Voluntary Consents</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        Consent for Electronic Contract Note (ECN)
                    </td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">23</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">23</td>
                </tr>
                <tr>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Rights and Obligations</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        Additional clauses forming part and parcel of mandatory Rights and Obligations.
                    </td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">33</td>
                </tr>
                <tr>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">20</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">DDPI</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Demat Debit and Pledge Instruction (DDPI)</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;"> </td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">&nbsp;</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">&nbsp;</td>
                </tr>
            </tbody>
        </table>
        <br />

        <table style="font-size:13px !important;">
            <tr style="border: 1px solid #000000;font-size: 11px;">
                <th style="border: 1px solid #000000;font-size: 11px;">Name of Stock Broker / Trading Member</th>
                <th style="border: 1px solid #000000;font-size: 11px;" colspan="3">FACILON SERVICES LIMITED</th>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">Single SEBI Regn. No.</th>
                <td style="border: 1px solid #000000;font-size: 11px;">SEBI Registration No. [To be filled]</td>
                <th style="border: 1px solid #000000;font-size: 11px;">Regn. Date</th>
                <td style="border: 1px solid #000000;font-size: 11px;">[Registration Date]</td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">SEBI Regn. No. NSDL</th>
                <td style="border: 1px solid #000000;font-size: 11px;" colspan="3">[NSDL DP Details]</td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">Clearing Member - NSE Commodity Derivatives</th>
                <td style="border: 1px solid #000000;font-size: 11px;" colspan="3"></td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">SEBI Regn. No.</th>
                <td style="border: 1px solid #000000;font-size: 11px;"></td>
                <th style="border: 1px solid #000000;font-size: 11px;">Regn. Date</th>
                <td style="border: 1px solid #000000;font-size: 11px;"></td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">Registered Office address</th>
                <td style="border: 1px solid #000000;font-size: 11px;" colspan="3">
                    <!-- Registered office address to be filled -->
                </td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">Registered / Correspondence Office</th>
                <td style="border: 1px solid #000000;font-size: 11px;" colspan="3">
                    Facilon Services<br />
                    Address Line 1<br />
                    City, State - Pincode, India
                </td>
            </tr>
        </table>
        <br />

        <table style="font-size:13px !important;">
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;"><span class="bold">Phone No.</span></th>
                <td style="border: 1px solid #000000;font-size: 11px;">+91-XXX-XXX-XXXX</td>
                <td style="border: 1px solid #000000;font-size: 11px;">+91-XXX-XXX-XXXX</td>
                <td style="border: 1px solid #000000;font-size: 11px;">
                    <span class="bold">Website:</span>
                    <a href="https://www.facilonservices.com/">www.facilonservices.com</a>
                </td>
            </tr>
        </table>
        <br />

        <table style="font-size:13px !important;">
            <thead>
                <tr>
                    <th style="border: 1px solid #000000;font-size: 11px;">Compliance Officer Name</th>
                    <th style="border: 1px solid #000000;font-size: 11px;">CEO Name</th>
                    <th style="border: 1px solid #000000;font-size: 11px;">Tel. No.</th>
                    <th style="border: 1px solid #000000;font-size: 11px;">Email</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td style="border: 1px solid #000000;font-size: 11px;">[Compliance Officer]</td>
                    <td style="border: 1px solid #000000;font-size: 11px;">[CEO Name]</td>
                    <td style="border: 1px solid #000000;font-size: 11px;">+91-XXX-XXX-XXXX</td>
                    <td style="border: 1px solid #000000;font-size: 11px;"><a href="mailto:compliance@facilonservices.com">compliance@facilonservices.com</a></td>
                </tr>
            </tbody>
        </table>
        <br />

        <p style="font-size: 11px;">
            <span style="letter-spacing: -0.3pt;">
                For any grievance / dispute please contact us at the above address or email id -
            </span>
            <a href="mailto:complaints@facilonservices.com">
                complaints@facilonservices.com
            </a>
            <span style="letter-spacing: -0.3pt;">
                and Phone no.
            </span>
            +91-XXX-XXX-XXXX. In case not satisfied with the response, please contact the relevant stock exchange or regulatory authority.
        </p>
    </div>

    <div class="page-break"></div>

    <!-- Instructions/Checklist Page -->
    <div class="WordSection3">
        <div>
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 50px; width: 200px;" />
        </div>

        <div class="row">
            <p style="font-size: 11px;" class="instructions-header">INSTRUCTIONS/CHECK LIST FOR FILLING KYC FORM</p>

            <p style="font-size: 11px;" class="list-paragraph">A. IMPORTANT POINTS:</p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                1. Self-attested copy of PAN card is mandatory for all clients, including Promoters/Partners/Karta/Trustees and whole-time directors and persons authorized to deal in securities on behalf of company/firm/others.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                2. Copies of all the documents submitted by the applicant should be self-attested and accompanied by originals for verification. In case the original of any document is not produced for verification, then the copies should be properly attested by entities authorized for attesting the documents, as per the below-mentioned list.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                3. If any proof of identity or address is in a foreign language / regional language, then translation into English is required.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                4. Name &amp; address of the applicant mentioned on the KYC form, should match with the documentary proof submitted.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                5. If correspondence &amp; permanent address are different, then proofs for both have to be submitted.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                6. Sole proprietor must make the application in his individual name &amp; capacity.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                7. For non-residents and foreign nationals, (allowed to trade subject to RBI and FEMA guidelines), copy of passport/PIO Card/ OCI Card and overseas address proof is mandatory.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                8. For foreign entities, CIN is optional; and in the absence of DIN no. for the directors, their passport copy should be given.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                9. In case of Merchant Navy NRI's, Mariner's declaration or certified copy of CDC (Continuous Discharge Certificate) is to be submitted.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                10. For opening an account with Depository participant or Mutual Fund, for a minor, photocopy of the School Leaving Certificate / Mark sheet issued by Higher Secondary Board / Passport of Minor / Birth Certificate must be provided.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                11. Politically Exposed Persons (PEP) are defined as individuals who are or have been entrusted with prominent public functions in a foreign country, e.g., Heads of States or of Governments, senior politicians, senior Government / judicial / military officers, senior executives of state owned corporations, important political party officials, etc.
            </p>
        </div>

        <div class="row">
            <p style="font-size: 11px;" class="list-paragraph">B. Proof of Identity (POI) : - List of documents admissible as Proof of Identity:</p>

            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                1. Unique Identification Number (UID) (Aadhaar)/ Passport/ Voter ID card/ Driving license.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                2. PAN card with photograph.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                3. Identity card / document with applicant's Photo, issued by any of the following: Central / State Government and its Departments, Statutory / Regulatory Authorities, Public Sector Undertakings, Scheduled Commercial Banks, Public Financial Institutions, Colleges affiliated to Universities, Professional Bodies such as ICAI, ICWAI, ICSI, Bar Council etc., to their Members; and Credit cards / Debit cards issued by Banks.
            </p>
        </div>

        <div class="row">
            <p style="font-size: 11px;" class="list-paragraph">C. Proof of Address : - List of documents admissible as Proof of Address :</p>
            <p style="text-align: justify; font-weight: normal;font-size: 11px;">
                (*Documents having an expiry date should be valid on the date of submission.)
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                1. Passport / Voters Identity Card / Ration Card / Registered Lease or Sale Agreement of Residence / Driving License / Flat Maintenance bill / Insurance Copy / Aadhaar Card.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                2. Utility bills like Telephone Bill (only land line), Electricity bill or Gas bill - Not more than 3 months old.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                3. Bank Account Statement / Passbook - Not more than 3 months old.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                4. Self-declaration by High Court and Supreme Court judges, giving the new address in respect of their own accounts.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                5. Proof of address issued by any of the following: Bank Managers of Scheduled Commercial Banks / Scheduled Co-Operative Bank / Multinational Foreign Banks / Gazetted Officer / Notary public / Elected representatives to the Legislative Assembly / Parliament / Documents issued by any Govt. or Statutory Authority.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                6. Identity card / document with address, issued by any of the following: Central / State Government and its Departments, Statutory / Regulatory Authorities, Public Sector Undertakings, Scheduled Commercial Banks, Public Financial Institutions, Colleges affiliated to Universities and Professional Bodies such as ICAI, ICWAI, ICSI, Bar Council etc., to their Members.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                7. For FII / sub account, Power of Attorney given by FII / sub-account to the Custodians (which are duly notarized and/or apostiled or consularised) that gives the registered address should be taken.
            </p>
        </div>

        <div class="row">
            <p style="font-size: 11px;" class="list-paragraph">D. Exemptions/clarifications to PAN :</p>
            <p style="text-align: justify; font-weight: normal;font-size:11px;">
                (*Sufficient documentary evidence in support of such claims to be collected.)
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                1. In case of transactions undertaken on behalf of Central Government and / or State Government and by officials appointed by Courts e.g. Official liquidator, Court receiver etc.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                2. Investors residing in the state of Sikkim.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                3. UN entities/multilateral agencies exempt from paying taxes / filing tax returns in India.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                4. SIP of Mutual Funds upto Rs 50,000 /- p.a.
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                5. In case of institutional clients, namely, FIIs, MFs, VCFs, FVCIs, Scheduled Commercial Banks, Multilateral and Bilateral Development Financial Institutions, State Industrial Development Corporations, Insurance Companies registered with IRDA and Public Financial Institution as defined under section 4A of the Companies Act, 1956, Custodians shall verify the PAN card details with the original PAN card and provide duly certified copies of such verified PAN details to the intermediary.
            </p>
        </div>

        <div class="row">
            <p style="font-size: 11px;" class="list-paragraph">E. List of people authorized to attest the documents :</p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                1. Notary Public, Gazetted Officer, Manager of a Scheduled Commercial / Co-operative Bank or Multinational Foreign Banks (Name, Designation &amp; Seal should be affixed on the copy).
            </p>
            <p style="margin-left: 40px; text-align: justify; font-weight: normal; font-size:11px;">
                2. In case of NRIs, authorized officials of overseas branches of Scheduled Commercial Banks registered in India, Notary Public, Court Magistrate, Judge, Indian Embassy / Consulate General in the country where the client resides are permitted to attest the documents.
            </p>
        </div>

        <p style="font-size: 11px; font-weight: bold;">
            F. Additional documents in case of trading in derivatives segments - illustrative list :
        </p>

        <table style="width: 100%;">
            <tr>
                <td>Copy of ITR Acknowledgement</td>
                <td>Copy of Annual Accounts</td>
            </tr>
            <tr>
                <td>In case of salary income - Salary Slip, Copy of Form 16</td>
                <td>Net worth certificate</td>
            </tr>
            <tr>
                <td>Copy of demat account holding statement.</td>
                <td>Bank account statement for last 6 months</td>
            </tr>
            <tr>
                <td>Any other relevant documents substantiating ownership of assets.</td>
                <td>Self declaration with relevant supporting documents.</td>
            </tr>
        </table>

        <p style="font-size: 11px; text-align: justify; margin-top: 10px;">
            *In respect of other clients, documents as per risk management policy of the stock broker need to be provided by the client from time to time.
        </p>

        <p style="font-size: 11px; font-weight: bold;">
            G. Copy of cancelled cheque leaf / pass book / bank statement specifying name of the constituent, MICR Code and IFSC Code of the bank should be submitted.
        </p>
        <p style="font-size: 11px; font-weight: bold;">
            H. Demat master or recent holding statement issued by DP bearing name of the client.
        </p>

        <p style="font-size: 11px; font-weight: bold; margin-bottom:0 !important;">
            I. For individuals :
        </p>

        <div class="row" style="margin:0!important;">
            <p style="margin:0 0 0 40px; text-align: justify; font-weight: normal; font-size:11px;">
                a. Stock broker has an option of doing 'in-person' verification through web camera at the branch office of the stock broker /sub- broker's office.
            </p>
            <p style="margin:0 0 0 40px; text-align: justify; font-weight: normal; font-size:11px;">
                b. In case of non-resident clients, employees at the stock broker's local office, overseas can do in-person' verification. Further, considering the infeasibility of carrying out 'In-person' verification of the non-resident clients by the stock broker's staff, attestation of KYC documents by Notary Public, Court, Magistrate, Judge, Local Banker, Indian Embassy / Consulate General in the country where the client resides may be permitted.
            </p>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- KYC Form - Part A -->
    <div class="WordSection4">
        <div>
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 50px; width: 200px;" />
        </div>

        <div class="row">
            <p style="font-size: 11px;" class="instructions-header">ACCOUNT OPENING FORM - PART A: KYC DETAILS</p>

            <h3 style="text-align: center; font-size: 14px;">PERSONAL INFORMATION</h3>

            <table style="width: 100%; font-size: 11px;">
                <tr>
                    <td style="width: 50%;"><strong>Investor Name:</strong></td>
                    <td style="width: 50%;">${personalInfo.investorFirstName!''} ${personalInfo.investorMiddleName!''} ${personalInfo.investorLastName!''}</td>
                </tr>
                <tr>
                    <td><strong>Date of Birth:</strong></td>
                    <td>${personalInfo.userDob!''}</td>
                </tr>
                <tr>
                    <td><strong>Gender:</strong></td>
                    <td>${personalInfo.gender!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Father's Name:</strong></td>
                    <td>${personalInfo.fatherNameTitle!''} ${personalInfo.fathersFirstName!''} ${personalInfo.fathersMiddleName!''} ${personalInfo.fathersLastName!''}</td>
                </tr>
                <tr>
                    <td><strong>Mother's Name:</strong></td>
                    <td>${personalInfo.motherNameTitle!''} ${personalInfo.motherFirstName!''} ${personalInfo.motherMiddleName!''} ${personalInfo.motherLastName!''}</td>
                </tr>
                <tr>
                    <td><strong>PAN Number:</strong></td>
                    <td>${personalInfo.userPanNo!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Citizenship:</strong></td>
                    <td>${personalInfo.citizenship!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Country of Residence:</strong></td>
                    <td>${personalInfo.countryOfResidence!'N/A'}</td>
                </tr>
            </table>

            <h3 style="text-align: center; font-size: 14px; margin-top: 30px;">CONTACT DETAILS</h3>

            <table style="width: 100%; font-size: 11px;">
                <tr>
                    <td colspan="2"><strong>RESIDENTIAL ADDRESS</strong></td>
                </tr>
                <tr>
                    <td style="width: 50%;"><strong>Address Line 1:</strong></td>
                    <td style="width: 50%;">${contactDetails.addressLine1!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Address Line 2:</strong></td>
                    <td>${contactDetails.addressLine2!''}</td>
                </tr>
                <tr>
                    <td><strong>City:</strong></td>
                    <td>${contactDetails.userCity!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>State:</strong></td>
                    <td>${contactDetails.userState!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Country:</strong></td>
                    <td>${contactDetails.userCountry!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Postal/Zip Code:</strong></td>
                    <td>${contactDetails.userZipCode!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Email:</strong></td>
                    <td>${contactDetails.email!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Mobile No:</strong></td>
                    <td>${contactDetails.mobileNo!'N/A'}</td>
                </tr>
            </table>

            <#if contactDetails.corrAddressLine1?has_content>
            <h4 style="font-size: 12px; margin-top: 20px;">CORRESPONDENCE ADDRESS</h4>
            <table style="width: 100%; font-size: 11px;">
                <tr>
                    <td style="width: 50%;"><strong>Address Line 1:</strong></td>
                    <td style="width: 50%;">${contactDetails.corrAddressLine1}</td>
                </tr>
                <#if contactDetails.corrAddressLine2?has_content>
                <tr>
                    <td><strong>Address Line 2:</strong></td>
                    <td>${contactDetails.corrAddressLine2}</td>
                </tr>
                </#if>
                <tr>
                    <td><strong>City:</strong></td>
                    <td>${contactDetails.corrUserCity!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>State:</strong></td>
                    <td>${contactDetails.corrUserState!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Country:</strong></td>
                    <td>${contactDetails.corrUserCountry!'N/A'}</td>
                </tr>
                <tr>
                    <td><strong>Postal/Zip Code:</strong></td>
                    <td>${contactDetails.corrUserZipCode!'N/A'}</td>
                </tr>
            </table>
            </#if>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- Bank Details -->
    <div class="WordSection5">
        <div>
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 50px; width: 200px;" />
        </div>

        <h3 style="text-align: center; font-size: 14px;">BANK ACCOUNT DETAILS</h3>

        <table style="width: 100%; font-size: 11px;">
            <tr>
                <td style="width: 50%;"><strong>Account Type:</strong></td>
                <td style="width: 50%;">${bankDetails.accountType!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Beneficiary Name:</strong></td>
                <td>${bankDetails.accountHolderName!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Bank Name:</strong></td>
                <td>${bankDetails.bankName!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Branch Name:</strong></td>
                <td>${bankDetails.branchName!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Bank Address:</strong></td>
                <td>${bankDetails.bankAddress!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Account Number:</strong></td>
                <td>${bankDetails.accountNumber!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>IFSC Code:</strong></td>
                <td>${bankDetails.ifscCode!'N/A'}</td>
            </tr>
            <#if bankDetails.rbiApproval?has_content>
            <tr>
                <td><strong>PIS Approval:</strong></td>
                <td>${bankDetails.rbiApproval}</td>
            </tr>
            </#if>
            <#if bankDetails.rbiApprovalOrderNumber?has_content>
            <tr>
                <td><strong>PIS Approval No:</strong></td>
                <td>${bankDetails.rbiApprovalOrderNumber}</td>
            </tr>
            </#if>
        </table>

        <h3 style="text-align: center; font-size: 14px; margin-top: 30px;">PASSPORT DETAILS</h3>

        <table style="width: 100%; font-size: 11px;">
            <tr>
                <td style="width: 50%;"><strong>Nationality:</strong></td>
                <td style="width: 50%;">${passportDetails.nationality!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Passport Number:</strong></td>
                <td>${passportDetails.passportNumber!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Date of Issue:</strong></td>
                <td>${passportDetails.dateOfIssue!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Place of Issue:</strong></td>
                <td>${passportDetails.placeOfIssue!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Valid Upto:</strong></td>
                <td>${passportDetails.validUpto!'N/A'}</td>
            </tr>
        </table>
    </div>

    <div class="page-break"></div>

    <!-- Tax Information -->
    <div class="WordSection6">
        <div>
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 50px; width: 200px;" />
        </div>

        <h3 style="text-align: center; font-size: 14px;">TAX INFORMATION</h3>

        <table style="width: 100%; font-size: 11px;">
            <tr>
                <td style="width: 50%;"><strong>Country of Tax Residence:</strong></td>
                <td style="width: 50%;">${taxInfo.currentCountryResidenceForTax!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Tax Identification Number:</strong></td>
                <td>${taxInfo.taxIdentificationNumber!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Identification Number Type:</strong></td>
                <td>${taxInfo.taxIdentificationNumberType!'N/A'}</td>
            </tr>
            <#if taxInfo.taxResidencyCertificateNo?has_content>
            <tr>
                <td><strong>Tax Residency Certificate No:</strong></td>
                <td>${taxInfo.taxResidencyCertificateNo}</td>
            </tr>
            </#if>
            <tr>
                <td><strong>US Person under FATCA:</strong></td>
                <td>${taxInfo.usPersonFatca!'N/A'}</td>
            </tr>
        </table>

        <h3 style="text-align: center; font-size: 14px; margin-top: 30px;">NOMINATION DETAILS</h3>

        <#list nominations as nomination>
        <h4 style="font-size: 12px;">Nominee ${nomination?index + 1}</h4>
        <table style="width: 100%; font-size: 11px;">
            <tr>
                <td style="width: 50%;"><strong>Nominee Name:</strong></td>
                <td style="width: 50%;">${nomination.nomineeName!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Relationship:</strong></td>
                <td>${nomination.relationship!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Percentage Share:</strong></td>
                <td>${nomination.percentageShare!'N/A'}%</td>
            </tr>
            <tr>
                <td><strong>Date of Birth:</strong></td>
                <td>${nomination.dateOfBirth!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Mobile No:</strong></td>
                <td>${nomination.mobileNo!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>PAN No:</strong></td>
                <td>${nomination.panNo!'N/A'}</td>
            </tr>
        </table>
        <br />
        </#list>
    </div>

    <div class="page-break"></div>

    <!-- Risk Profile / Other Information -->
    <div class="WordSection7">
        <div>
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 50px; width: 200px;" />
        </div>

        <h3 style="text-align: center; font-size: 14px;">OTHER INFORMATION / RISK PROFILE</h3>

        <table style="width: 100%; font-size: 11px;">
            <tr>
                <td style="width: 50%;"><strong>Source of Funds:</strong></td>
                <td style="width: 50%;">${riskProfile.sourceOfFunds!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Source of Wealth:</strong></td>
                <td>${riskProfile.sourceOfWealth!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Education Qualification:</strong></td>
                <td>${riskProfile.educationalQualification!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Gross/Monthly Income:</strong></td>
                <td>${riskProfile.grossIncome!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Net Worth:</strong></td>
                <td>${riskProfile.netWorth!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Occupation:</strong></td>
                <td>${riskProfile.occupation!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Line of Business:</strong></td>
                <td>${riskProfile.lineOfBusiness!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Nature of Organization:</strong></td>
                <td>${riskProfile.natureOfOrganisation!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Politically Exposed:</strong></td>
                <td>${riskProfile.politicallyExposed!'NO'}</td>
            </tr>
            <tr>
                <td><strong>Related to Politically Exposed Person:</strong></td>
                <td>${riskProfile.relatedToPoliticallyExposed!'NO'}</td>
            </tr>
            <tr>
                <td><strong>Investment Experience (Years):</strong></td>
                <td>${riskProfile.investmentExperienceYears!'N/A'}</td>
            </tr>
            <tr>
                <td><strong>Investment Experience In:</strong></td>
                <td>${riskProfile.investmentExperienceIn!'N/A'}</td>
            </tr>
        </table>
    </div>

    <div class="page-break"></div>

    <!-- TARIFF SCHEDULE - EQUITY -->
    <div class="WordSectionneww100">
        <div style="text-align: right;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>
        <div>
            <p style="font-size: 11px; text-align: center;font-weight: bold; font-size: 16px;">TARIFF SCHEDULE FOR EQUITY SEGMENT</p>
        </div>

        <table style="width: 100%; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 11px; border: 1px solid black;">
            <tr style="background-color: #f2f2f2;">
                <th style="border: 1px solid black; padding: 5px; font-weight: bold;">Segment</th>
                <th style="border: 1px solid black; padding: 5px; font-weight: bold;">Brokerage</th>
                <th style="border: 1px solid black; padding: 5px; font-weight: bold;">Service Tax</th>
                <th style="border: 1px solid black; padding: 5px; font-weight: bold;">Remarks</th>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Cash Segment (BSE &amp; NSE)</td>
                <td style="border: 1px solid black; padding: 5px;">____% / Rs. __________</td>
                <td style="border: 1px solid black; padding: 5px;"></td>
                <td style="border: 1px solid black; padding: 5px;">Delivery / Intraday</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Futures - Equity</td>
                <td style="border: 1px solid black; padding: 5px;">____% / Rs. __________</td>
                <td style="border: 1px solid black; padding: 5px;"></td>
                <td style="border: 1px solid black; padding: 5px;">All transaction</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Options - Equity</td>
                <td style="border: 1px solid black; padding: 5px;">____% / Rs. __________</td>
                <td style="border: 1px solid black; padding: 5px;"></td>
                <td style="border: 1px solid black; padding: 5px;">All transaction</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Currency Derivative</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. __________ per lot</td>
                <td style="border: 1px solid black; padding: 5px;"></td>
                <td style="border: 1px solid black; padding: 5px;"></td>
            </tr>
        </table>

        <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px; border:none;">
            <tr style="border:none;">
                <th colspan="2" style="padding: 5px; text-align: left; border:none;font-weight: bold;">MUTUAL FUND SEGMENT</th>
            </tr>
            <tr style="padding: 5px;">
                <td style="border: none;">Brokerage on redemption (excluding Liquid Fund)</td>
                <td style="border: none;">Rs. 50/- per transaction</td>
            </tr>
            <tr style="border:none;">
                <td colspan="2" style="padding: 5px; border:none;">Currency Derivative Rs. 20/- per lot on single leg of transaction.</td>
            </tr>
        </table>

        <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px; border:none;">
            <tr style="border:none;">
                <th colspan="2" style="padding: 5px; text-align: left; border:none;font-weight: bold;">OTHER LEVIES AS PREVALENT FROM TIME TO TIME :-</th>
            </tr>
            <tr style="border:none;">
                <td colspan="2" style="padding: 5px; text-align: justify; border:none;">Other Charges includes:- 1. Goods and Services Tax 2. Stamp Duty 3. Exchange transaction charges 4. SEBI turnover fees 5. Clearing Member charges 6. Securities Transaction Tax (STT) 7. Expiry charges on derivative contracts 8. Delayed payment charges not exceeding 2.5% per month. 9. Cheque return charges in actual as charged by the banks. 10. Demat charges towards pay-in / pay out of securities / margin from beneficiary account. 11. Amount paid / payable on account of any penalties / charges levied due to default / breach committed by client. 12. Document and service charges for trading account opening. 13. Charges for providing research report. 14. Any other statutory levies.</td>
            </tr>
        </table>

        <br /><br />
        <table style="width: 100%; border:none;">
            <tr style="border:none;">
                <td style="padding: 5px; text-align: left; border:none;"><b>Name of the Applicant: <u>${personalInfo.investorFirstName!''} ${personalInfo.investorMiddleName!''} ${personalInfo.investorLastName!''}</u></b></td>
            </tr>
            <tr style="border:none;">
                <td style="padding: 5px; text-align: left; border:none;">
                    <b>Signature of the Applicant _______________________________</b>
                </td>
            </tr>
        </table>
    </div>

    <div class="page-break"></div>

    <!-- SCHEDULE OF DP CHARGES -->
    <div class="WordSectionneww100">
        <div style="text-align: left;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>
        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>

        <table style="width: 100%; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 11px; border: 1px solid black;">
            <tr>
                <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">Depository Participant ID [To be filled]</td>
            </tr>
            <tr style="background-color: #f2f2f2;">
                <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">Charges for Depository Services</td>
            </tr>
            <tr>
                <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">The Investor will have to choose one of the schemes to pay the charges for the services offered.</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;" colspan="1">PARTICULARS Please Select any one scheme</td>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">SCHEME - A <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center;"></div></td>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">SCHEME - B <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center;"></div></td>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">SCHEME - C <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center;"></div></td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;"> </td>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">With POA</td>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Without POA</td>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">With POA</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">A) Documentation Charges</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">B) Refundable Deposit (Non Interest bearing)</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 3,000/-</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">C) Account Maintenance</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Payable at the time of opening the Account</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 3,000/-</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Custody Charges</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Dematerialisation Charges (Per Request)</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 200/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 200/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 200/-</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Rematerialisation Charges (Per Request)</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Transaction Charges</td>
                <td style="border: 1px solid black; padding: 5px;"></td>
                <td style="border: 1px solid black; padding: 5px;"></td>
                <td style="border: 1px solid black; padding: 5px;"></td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">With Service Provider (Buy)</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Within Service Provider (Sell)</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 20/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 12/-</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Outside Service Provider (Sell)</td>
                <td style="border: 1px solid black; padding: 5px;">Rs.50/- or 0.05% of value whichever is higher</td>
                <td style="border: 1px solid black; padding: 5px;">Rs.50/- or 0.05% of value whichever is higher</td>
                <td style="border: 1px solid black; padding: 5px;">Rs.50/- or 0.05% of value whichever is higher</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Pledge creation</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
                <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Pledge closure</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
            </tr>
            <tr>
                <td style="border: 1px solid black; padding: 5px;">Pledge invocation</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
                <td style="border: 1px solid black; padding: 5px;">Nil</td>
            </tr>
        </table>

        <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px; border:none;">
            <tr style="border:none;">
                <th colspan="2" style="padding: 5px; text-align: left; border:none;font-weight: bold;">Notes :-</th>
            </tr>
            <tr style="border:none;">
                <td colspan="2" style="padding: 5px; text-align: justify; border:none;">
                    1) Cheque returned charges will be levied in actual as charged by the banks.<br />
                    2) Interest @ 13% p.a. shall be charged, if the bill is not paid by due date.<br />
                    3) In case of Corporate Demat Account: AMC of Rs. 500/- p.a. will be levied by NSDL in addition to charges.<br />
                    4) The above rates are based on the existing NSDL charges and may change from time to time.<br />
                    5) The scheme once selected can be changed only at the end of financial year.<br />
                    6) Refundable deposit will be repaid only on closure of account. No adjustment will be made in the interim.<br />
                    7) Any extra statement would be charged @ Rs. 25/- per statement for one page and thereafter @ Rs. 2/- per page.<br />
                    8) Statutory levies as applicable would be charged extra.
                </td>
            </tr>
        </table>

        <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px; border:none;">
            <tr>
                <td style="padding: 5px; text-align: center; font-weight: bold; border: 1px solid black;">Declaration for Basic Service Demat Account (BSDA)</td>
            </tr>
            <tr style="border:none;">
                <td style="padding: 5px; border:none;">Please select any one option given below,</td>
            </tr>
            <tr style="border:none;">
                <td style="padding: 5px; border:none;">
                    1. <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center;"></div> I/We wish to open Regular Demat Account.
                </td>
            </tr>
            <tr style="border:none;">
                <td style="padding: 5px; border:none;">
                    2. <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center;"></div> I/We wish to open BSDA Account.
                </td>
            </tr>
        </table>
    </div>

    <div class="page-break"></div>

    <!-- ACKNOWLEDGEMENT LETTER -->
    <div class="WordSectionneww100">
        <div style="text-align: right;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>

        <div style="font-family: Arial, sans-serif; font-size: 14px; padding: 20px; color: #000; line-height: 1.6;">
            <div style="text-align: center; font-weight: bold; font-size: 16px; margin-bottom: 20px;">
                ACKNOWLEDGEMENT LETTER
            </div>

            <p style="font-weight: normal; font-size: 11px;">
                This is to acknowledge the receipt of the following documents from:
            </p>

            <table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
                <tr>
                    <td style="width: 30%; padding: 5px; font-weight: bold;">Name:</td>
                    <td style="width: 70%; padding: 5px; border-bottom: 1px solid #000;">${personalInfo.investorFirstName!''} ${personalInfo.investorMiddleName!''} ${personalInfo.investorLastName!''}</td>
                </tr>
                <tr>
                    <td style="padding: 5px; font-weight: bold;">Client Code:</td>
                    <td style="padding: 5px; border-bottom: 1px solid #000;">${investor.uniqueCode!''}</td>
                </tr>
                <tr>
                    <td style="padding: 5px; font-weight: bold;">Date:</td>
                    <td style="padding: 5px; border-bottom: 1px solid #000;">___________________</td>
                </tr>
            </table>

            <p style="font-weight: bold; font-size: 11px; margin-top: 20px;">Documents Received:</p>
            <ol style="font-size: 11px; line-height: 1.8;">
                <li>Account Opening Form (KYC Form Part A &amp; B)</li>
                <li>PAN Card Copy (Self-attested)</li>
                <li>Address Proof (Self-attested)</li>
                <li>Bank Proof (Cancelled Cheque / Bank Statement)</li>
                <li>Passport Size Photographs</li>
                <li>Signature Specimen</li>
                <li>FATCA/CRS Declaration Form</li>
                <li>Rights and Obligations Document</li>
                <li>Risk Disclosure Document</li>
                <li>Tariff Schedule</li>
                <li>DP Charges Schedule</li>
                <li>Other Consent Forms</li>
            </ol>

            <p style="font-size: 11px; margin-top: 20px;">
                We confirm that we have received the above-mentioned documents in good order and the same have been verified.
            </p>

            <br /><br /><br />

            <table style="width: 100%; border: none;">
                <tr>
                    <td style="width: 50%; text-align: left; border: none;">
                        _____________________________<br />
                        <strong>For Facilon Services Limited</strong><br />
                        Authorized Signatory
                    </td>
                    <td style="width: 50%; text-align: right; border: none;">
                        Date: ___________________
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- FATCA/CRS DECLARATION FORM -->
    <div class="WordSectionneww100">
        <div style="text-align: left;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>

        <table style="font-size: 11px; margin-top:10px; width:100%; border:none;">
            <tr style="border:none;">
                <td colspan="5" style="font-weight: bold; width:60%; font-size:15px; border:none;">SELF-CERTIFICATION FOR INDIVIDUAL</td>
                <td colspan="2" style="padding: 5px; border: 1px solid #000; font-weight: bold; width:10%;">Client Code:</td>
                <td colspan="3" style="padding: 5px; border: 1px solid #000; font-weight: bold; width:30%;">${investor.uniqueCode!''}</td>
            </tr>
            <tr>
                <td colspan="10" style="font-weight: bold; text-align:center; font-size:15px; border:none;">FATCA/CRS DECLARATION FORM</td>
            </tr>
            <tr>
                <td colspan="10" style="font-weight: bold; text-align:left; border: 1px solid #000;">Part I- Please fill in the country for each of the following:</td>
            </tr>
            <tr>
                <td style="text-align:left; border: 1px solid #000;">1.</td>
                <td colspan="9" style="text-align:left; border: 1px solid #000;">Country Of:</td>
            </tr>
            <tr>
                <td colspan="1" style="font-weight: bold; border: 1px solid #000; width: 10%;"></td>
                <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%;">a)</td>
                <td colspan="3" style="padding: 5px; border: 1px solid #000; width: 30%;">Birth</td>
                <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;">${taxInfo.countryOfBirth!'India'}</td>
            </tr>
            <tr>
                <td colspan="1" style="font-weight: bold; border: 1px solid #000; width: 10%;"></td>
                <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%;">b)</td>
                <td colspan="3" style="padding: 5px; border: 1px solid #000; width: 30%;">Citizenship</td>
                <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;">${personalInfo.citizenship!'India'}</td>
            </tr>
            <tr>
                <td colspan="1" style="font-weight: bold; border: 1px solid #000; width: 10%;"></td>
                <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%;">c)</td>
                <td colspan="3" style="padding: 5px; border: 1px solid #000; width: 30%;">Residence for Tax Purposes</td>
                <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;">${taxInfo.currentCountryResidenceForTax!'India'}</td>
            </tr>
            <tr>
                <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%;">2.</td>
                <td colspan="4" style="padding: 5px; border: 1px solid #000; width: 40%;">US Person (Yes / No)</td>
                <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;">${taxInfo.usPersonFatca!'NO'}</td>
            </tr>
            <tr>
                <td colspan="10" style="font-weight: bold; text-align:left; border: 1px solid #000;">Part II- Please note:</td>
            </tr>
            <tr>
                <td colspan="1" style="text-align:left; border: 1px solid #000; width: 10%;"></td>
                <td colspan="1" style="text-align:left; border: 1px solid #000; width: 10%;">a.</td>
                <td colspan="8" style="text-align:left; border: 1px solid #000; width: 80%;">If in all fields above, the country mentioned by you is India and if you do not have US person status, please proceed to part III for signature.</td>
            </tr>
            <tr>
                <td colspan="1" style="text-align:left; border: 1px solid #000; width: 10%;"></td>
                <td colspan="1" style="text-align:left; border: 1px solid #000; width: 10%;">b.</td>
                <td colspan="8" style="text-align:left; border: 1px solid #000; width: 80%;">If for any of the above field, the country mentioned by you is not India and/or if your US person status is Yes, please provide the Tax Payer Identification Number (TIN) or functional equivalent.</td>
            </tr>
            <tr>
                <td colspan="1" style="text-align:left; border: 1px solid #000; width: 10%;">i)</td>
                <td colspan="4" style="text-align:left; border: 1px solid #000; width: 40%;">TIN: ${taxInfo.taxIdentificationNumber!''}</td>
                <td colspan="2" style="text-align:left; border: 1px solid #000; width: 20%;">Country of Issue</td>
                <td colspan="3" style="text-align:left; border: 1px solid #000; width: 30%;">${taxInfo.currentCountryResidenceForTax!''}</td>
            </tr>
        </table>

        <table style="width:100%; border-collapse: collapse; font-size: 11px; border: 1px solid #000; margin-top:10px;">
            <tr>
                <td style="padding: 5px; border:none;" colspan="2">
                    <strong>Part III- Customer Declaration (Applicable for all customers)</strong><br /><br />
                    <strong>(i)</strong> Under penalty of perjury, I/we certify that the information provided on this form is true, correct, and complete.
                </td>
            </tr>
            <tr>
                <td style="padding: 5px; border:none;" colspan="2">
                    <strong>(ii)</strong> I/We understand that the Service Provider is relying on this information for determining the status in compliance with FATCA/CRS.
                </td>
            </tr>
            <tr>
                <td style="padding: 5px; border:none;" colspan="2">
                    <strong>(iii)</strong> I/We agree to submit a new form within 30 days if any information or certification on this form becomes incorrect.
                </td>
            </tr>
            <tr>
                <td style="padding: 5px; border:none;" colspan="2">
                    <strong>(iv)</strong> I/We agree that as may be required by domestic regulators/tax authorities, reportable details may be reported to CBDT or account may be closed or suspended.
                </td>
            </tr>
            <tr>
                <td style="padding: 5px; border:none;" colspan="2">
                    <strong>(v)</strong> I/We certify that I/We provide the information on this form and to the best of my/our knowledge and belief the certification is true, correct, and complete including the taxpayer identification number of the applicant.
                </td>
            </tr>
        </table>

        <table style="width:100%; border-collapse: collapse; font-size: 11px;" cellpadding="5">
            <tr>
                <td style="width: 50%; padding: 8px; border: 1px solid #000;"><strong>Signature:</strong></td>
                <td style="width: 50%; padding: 8px; border: 1px solid #000;"></td>
            </tr>
            <tr>
                <td style="padding: 8px; border: 1px solid #000;"><strong>Name: ${personalInfo.investorFirstName!''} ${personalInfo.investorMiddleName!''} ${personalInfo.investorLastName!''}</strong></td>
                <td style="padding: 8px; border: 1px solid #000;"><strong>Date (DD/MM/YYYY):</strong></td>
            </tr>
        </table>
    </div>

    <div class="page-break"></div>

    <!-- CONSENT FOR AADHAAR USAGE -->
    <div class="WordSectionneww100">
        <div style="text-align: right;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>

        <div style="font-family: Arial, sans-serif; font-size: 14px; width: 100%; line-height: 1.5;">
            <div style="text-align: center; font-weight: bold; font-size: 16px; margin-bottom: 20px;">
                MANDATORY WHERE AADHAAR IS PART OF KYC DOCUMENT
            </div>

            <div style="margin-bottom: 20px;">
                Date: ____________________________
            </div>

            <div style="margin-bottom: 10px;">
                To,<br />
                NSDL Database Management Limited (NDML)<br />
                4th Floor, Trade World, A Wing,<br />
                Kamala Mills Compound, Lower Parel,<br />
                Mumbai - 400 013
            </div>

            <div style="margin-bottom: 10px;">
                Dear Sir / Madam,
            </div>

            <div style="font-weight: bold; margin-bottom: 5px;">
                Sub: - Consent for usage of Aadhaar Number
            </div>

            <div style="text-align: justify; font-size: 11px;">
                I/We hereby declare that the KYC details furnished by me are true and correct to the best of my/our knowledge and belief and I/we undertake to inform you of any changes therein, immediately. In case any of the above information is found to be false or untrue or misleading or misrepresenting, I am/we are aware that I/We may be held liable for it. I am aware of other modes of KYC which are available, and I have chosen Aadhaar based method voluntarily. My Aadhaar record can be used by NDML KRA only for the specific purpose of validating /maintaining / sharing my KYC record and as audit evidence. I will have an option to request for deletion of my Aadhaar record.
                <br /><br />
                I/We hereby consent to receiving information from NDML KRA through SMS/Email on the above registered number/Email address.
                <br /><br />
                I am/ we are also aware that for Aadhaar OVD based KYC, my KYC request shall be validated against Aadhaar details.
                <br /><br />
                I/We hereby consent to sharing my/our masked Aadhaar card with readable QR code or my Aadhaar XML/Digi locker XML file, along with passcode and as applicable, with KRA and other Intermediaries with whom I have a business relationship for KYC purposes only.
            </div>

            <br />

            <table style="width: 100%; border-collapse: collapse; font-family: sans-serif; font-size: 11px; text-align: left; margin-top:10px;" cellpadding="8">
                <thead>
                    <tr>
                        <th style="width: 35%; padding: 8px; border: 1px solid #000; font-weight: bold;"></th>
                        <th style="width: 35%; padding: 8px; border: 1px solid #000; font-weight: bold;">Name(s) of holder(s)</th>
                        <th style="width: 30%; padding: 8px; border: 1px solid #000; font-weight: bold;">Signature(s) of holder</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Sole / First Holder (Mr./Ms.)</td>
                        <td style="padding: 8px; border: 1px solid #000;">${personalInfo.investorFirstName!''} ${personalInfo.investorLastName!''}</td>
                        <td style="padding: 8px; border: 1px solid #000;"></td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Second Holder (Mr./Ms.)</td>
                        <td style="padding: 8px; border: 1px solid #000;"></td>
                        <td style="padding: 8px; border: 1px solid #000;"></td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Third Holder (Mr./Ms.)</td>
                        <td style="padding: 8px; border: 1px solid #000;"></td>
                        <td style="padding: 8px; border: 1px solid #000;"></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- ELECTRONIC CONTRACT NOTE (ECN) DECLARATION -->
    <div class="WordSectionneww100">
        <div style="text-align: right;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>

        <div style="font-family: sans-serif; font-size: 11px; line-height: 1.6;">
            <div style="text-align: right; font-weight: bold; font-size: 14px;">Appendix A</div>

            <div style="text-align: center; font-weight: bold; font-size: 16px; margin-bottom: 20px;">
                ELECTRONIC CONTRACT NOTE (ECN) - DECLARATION
            </div>

            <p>
                To,<br />
                Facilon Services Limited,<br />
                [Address Line 1]<br />
                [City, State - Pincode]
            </p>

            <p>Dear Sir,</p>

            <p style="font-weight: normal;">
                I, <u><strong>${personalInfo.investorFirstName!''} ${personalInfo.investorMiddleName!''} ${personalInfo.investorLastName!''}</strong></u>, a client with member M/s. <strong>FACILON SERVICES LIMITED</strong> undertake as follows:
            </p>

            <ul style="margin-top: 0;">
                <li>
                    I am aware that the Member has to provide physical contract note in respect of all the trades placed by me unless I myself want the same in the electronic form. I am voluntarily requesting for delivery of electronic contract note pertaining to all the trades carried out / ordered by me.
                </li>
                <li>
                    My email ID is <u><strong>${contactDetails.email!'[Email not provided]'}</strong></u> and/or secondary email ID <span style="border-bottom: 1px solid #000; display: inline-block; width: 250px;">&nbsp;</span>
                </li>
                <li>
                    I am aware that non-receipt of bounced mail notification by the member shall amount to delivery of the contract note at the above email id.
                </li>
                <li>
                    This authorization has been signed by me only and not by any authorised person on my behalf or holder of the Power of Attorney.
                </li>
                <li>
                    Please note that any change in my email-id shall be communicated by me through a physical letter to Facilon Services Limited.
                </li>
            </ul>

            <br /><br />

            <p>
                <strong>Signature of the Applicant</strong>: <span style="border-bottom: 1px solid #000; display: inline-block; width: 100px;"></span>
            </p>

            <p>
                <strong>Date</strong>: _________________<br /><br />
                <strong>Place</strong>: ${contactDetails.userCity!''}
            </p>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- CONSENT & MOST IMPORTANT TERMS AND CONDITIONS (MITC) -->
    <div class="WordSectionneww100">
        <div style="text-align: right;">
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 61px; width: 150px;" />
        </div>

        <div style="font-family: Arial, sans-serif; font-size: 11px; line-height: 1.6;">
            <div style="text-align: center; font-weight: bold; font-size: 16px; margin-bottom: 20px;">
                MOST IMPORTANT TERMS AND CONDITIONS (MITC)
            </div>

            <p style="font-weight: bold;">1. Brokerage Charges:</p>
            <p>The brokerage and other charges as per the tariff schedule are applicable to all transactions.</p>

            <p style="font-weight: bold;">2. Rights and Obligations:</p>
            <p>The client agrees to abide by the rights and obligations as specified in the agreement and regulatory framework.</p>

            <p style="font-weight: bold;">3. Risk Disclosure:</p>
            <p>The client acknowledges that trading in securities and derivatives involves risks and has read and understood the Risk Disclosure Document.</p>

            <p style="font-weight: bold;">4. Margin Requirements:</p>
            <p>The client agrees to maintain adequate margins as per exchange requirements and broker policies.</p>

            <p style="font-weight: bold;">5. Settlement of Funds:</p>
            <p>Funds and securities shall be settled as per the settlement cycle prescribed by the exchanges.</p>

            <p style="font-weight: bold;">6. Contract Notes:</p>
            <p>Contract notes shall be issued for all transactions as per regulatory requirements.</p>

            <p style="font-weight: bold;">7. Statements:</p>
            <p>Account statements shall be provided periodically as per regulatory guidelines.</p>

            <p style="font-weight: bold;">8. Dispute Resolution:</p>
            <p>Any disputes shall be subject to arbitration as per the arbitration agreement.</p>

            <p style="font-weight: bold;">9. Termination:</p>
            <p>Either party may terminate the agreement subject to settlement of all outstanding obligations.</p>

            <p style="font-weight: bold;">10. Amendments:</p>
            <p>The terms and conditions may be amended from time to time with prior notice to the client.</p>

            <br /><br />

            <p style="font-weight: bold;">
                I/We acknowledge that I/we have read, understood and agree to the Most Important Terms and Conditions mentioned above.
            </p>

            <br /><br />

            <table style="width: 100%; border: none;">
                <tr>
                    <td style="width: 50%; text-align: left; border: none;">
                        _____________________________<br />
                        <strong>Signature of Client</strong><br />
                        Name: ${personalInfo.investorFirstName!''} ${personalInfo.investorLastName!''}
                    </td>
                    <td style="width: 50%; text-align: right; border: none;">
                        Date: ___________________
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- DECLARATION AND SIGNATURE SECTION -->
    <div class="WordSection8">
        <div>
            <img src="https://www.facilonservices.com/images/logo.png" alt="Facilon Logo" style="height: 50px; width: 200px;" />
        </div>

        <h3 style="text-align: center; font-size: 14px;">FINAL DECLARATION</h3>

        <p style="font-size: 11px; text-align: justify;">
            I/We hereby declare that the details furnished above are true and correct to the best of my/our knowledge and belief and I/We undertake to inform you of any changes therein, immediately. In case any of the above information is found to be false or untrue or misleading or misrepresenting, I am/we are aware that I/we may be held liable for it.
        </p>

        <p style="font-size: 11px; text-align: justify;">
            I/We hereby confirm that I am/we are not politically exposed person(s) (PEP). I/We further confirm that I/We have read and understood the contents of the Rights and Obligations, Risk Disclosure Document, Guidance Note, Policies and Procedures and other documents provided to me/us and agree to be bound by the same.
        </p>

        <br /><br />

        <table style="width: 100%; border: none;">
            <tr>
                <td style="width: 50%; text-align: left; border: none;">
                    <strong>Place:</strong> ___________________
                </td>
                <td style="width: 50%; text-align: right; border: none;">
                    <strong>Date:</strong> ___________________
                </td>
            </tr>
        </table>

        <br /><br /><br />

        <table style="width: 100%; border: none;">
            <tr>
                <td style="width: 50%; text-align: left; border: none; vertical-align: bottom;">
                    _____________________________<br />
                    <strong>Signature of Applicant</strong><br />
                    Name: ${personalInfo.investorFirstName!''} ${personalInfo.investorLastName!''}
                </td>
                <td style="width: 50%; text-align: right; border: none; vertical-align: bottom;">
                    _____________________________<br />
                    <strong>For Office Use Only</strong><br />
                    Authorized Signatory
                </td>
            </tr>
        </table>
    </div>

    <!-- Additional pages for Rights &amp; Obligations, Risk Disclosure, Guidance Notes, etc. can be added here -->
    <!-- NOTE: This template provides the basic structure. Additional regulatory documents -->
    <!-- (Rights &amp; Obligations, Risk Disclosure, Tariff Schedule, etc.) should be added -->
    <!-- as separate sections with appropriate page breaks -->

</body>
</html>

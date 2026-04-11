<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="X-UA-Compatible" content="ie=edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>Ventura Form</title>

    <style>
        @page {
            margin: 0 20px;
        }

        body {
            font-family: 'Arial Narrow', Arial, sans-serif;
            font-size: 12pt;
            margin: 0;
            padding: 0;
        }

        /* Style for paragraph element */
        p {
            text-align: justify;
            font-weight: 700;
        }

        /* Style for anchor tag */
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
        .main-table-row tr{
            border:none;
        }
        .main-table-row td{
            border:none;
            padding:5px;
        }
        .main-table-row th{
            border:none;
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
            max-height: calc(100% - 220px);
            /* Prevents overflow */
            overflow: hidden;
        }

        .form-section p {
            margin: 10px 0;
            font-size: 16px;
        }

        .footer {
            margin-top: 60px;
            width: 100%;
            background-color: #f58220;
            text-align: center;
            padding: 10px 0;
            color: #fff;
            font-size: 18px;
            font-weight: bold;
            position: absolute;
        }

        .footer span {
            display: block;
            font-size: 14px;
            margin-top: 5px;
            font-weight: normal;
        }

        .WordSection2 {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
        }

        table {
            /*border: 1px solid #f2f2f2;*/
            border: none;
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            /*border: 1px solid #000000;*/
            border: none;
            padding: 8px;
            text-align: left;
        }
        .underline {
            /* border-bottom: 1px solid #000; */
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
        }

        .list-paragraph {
            margin-top: 5px;
            margin-left: 65px;
            text-align: justify;
        }

        .list-paragraph b {
            font-weight: bold;
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

        .WordSection3 {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            position: relative;
            overflow: hidden;
        }

        .instructions-header {
            font-weight: bold;
        }

        .list-paragraph {
            font-size: 16px;
            margin: 10px 0;
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
        .square-box {
            display: inline-block;
            width: 10px;
            height: 10px;
            border: 1px solid #000;
            /*margin: 2px;*/
        }

        td div {
            display: flex;
            gap: 5px; /* Optional spacing between boxes */
            align-items: center;
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

    #tab3 {
          position: relative;
        }

        #tab3::before {
          content: "_______________________________________________________________________________________________________________________";
          position: absolute;
          top: 70%;
          left: -15%;
          /*right: 10%;*/
          transform: translate(-100%, -100%) rotate(-40deg);
          font-size: 2rem;
          color: black;
          white-space: nowrap;
          pointer-events: none;
          z-index: 2;


        }
        #tab3 * {
          position: relative;
          z-index: 1;
        }

      table.pdf-bordered {
        border-collapse: collapse;
        width: 100%;
        font-size: 11px;
      }

      table.pdf-bordered td, table.pdf-bordered th {
        border: 1px solid #000;
        padding: 6px;
      }

      p.list-paragraph {
        font-size: 11px;
      }

      .note-text {
        text-align: justify;
        font-size: 11px;
        font-weight: normal;
      }

      table, th, td {
        border: 1px solid #000;
        border-collapse: collapse;
        font-size: 11px;
      }
      th, td {
        padding: 2px;
        text-align: left;
        vertical-align: top;
      }

        .PART-II-TRADING{
            border:1px solid #000;
        }
        .PART-II-TRADING td{
            border:1px solid #000;
            padding:3px;
        }

          .new-table-border{
              border:none;
          }
           .new-table-border th{
              border:none;
          }
          .new-table-border tr{
              border:none;
          }
         .new-table-border td{
              border:none;
              padding:5px;
          }


    .n-a-bg-custom-sp {
        position: relative;
        background: #fff;
        width: 100%;
        height: 100%;
    }

    /* diagonal line */
    .na-diagonal-line {
        position: absolute;
        left: -20%;
        top: 50%;
        width: 140%;
        height: 2px;
        background: #000;
        transform: rotate(-45deg);
        opacity: 0.2;
    }

    /* diagonal N/A text */
    .na-text {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%) rotate(-45deg);
        font-size: 80px;
        font-weight: 700;
        color: #000;
        opacity: 0.3;
        white-space: nowrap;
    }


    .na-watermark-diagonal {
                position: relative;
                background: #fff;
            }

            /* diagonal line */
            .na-watermark-diagonal::before {
                content: "";
                position: absolute;
                left: -20%;
                top: 50%;
                width: 140%;
                height: 2px;
                background: #000;
                transform: rotate(-45deg);
                opacity: 0.2;
            }

            /* diagonal N/A text */
            .na-watermark-diagonal::after {
                content: "N/A";
                position: absolute;
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%) rotate(-45deg);
                font-size: 80px;
                font-weight: 700;
                color: #000;
                opacity: 0.3;
                white-space: nowrap;
            }

        .table td, .table th {
            padding: 2px;
        }

    .custom-mar-para-one {
        padding-left: 25px;
            padding-right: 25px;
    }

    .custom-mar-para-one p {
        margin-bottom: 5px;
        margin-top: 5px;
    }
    </style>
</head>

<body>
    <!-- First Page with Design -->
    <div class="WordSection1">
        <!-- Header Section -->
        <div class="header" style="margin-top: 20px;"></div>

        <!-- Logo -->
        <div class="logo">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" />
        </div>

        <!-- Form Section -->
        <div class="form-section">
            <p>Name Of Client :
                <span style="display: inline-block; width: 50%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: left;">
                    ${(data.firstName!'') + ' ' + (data.lastName!'')}
                </span>
            </p>
            <p>Client Code :
                <span style="display: inline-block; width: 25%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: center;">
                    ${data.clientCode!''}
                </span>
                &nbsp;&nbsp;
                Client ID :
                <span style="display: inline-block; width: 25%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: center;">
                    ${data.clientId!''}
                </span>
            </p>
            <p>Branch :
                <span style="display: inline-block; width: 50%; padding-bottom: 2px; border-bottom: 1px solid #000; text-align: left;">

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

    <!-- Second Page with Content -->
    <div class="WordSection2">

        <p class="MsoBodyText">
        </p>
        <table border="1" style="border-collapse: collapse; border-color: #000000; width: 20%; float: right;">
            <tr>
                <td style="padding: 0px; text-align: center; font-weight: bold;">
                    PART - A
                </td>
            </tr>
        </table>

        <div style="text-align: center; margin-top: 0; margin-right: 0in; margin-bottom: 0; margin-left: 0; font-weight: 600;font-size:14px;">
            ACCOUNT<span style="letter-spacing: .5pt;"> </span>
            OPENING<span style="letter-spacing: .5pt;"> </span>
            KIT <span style="letter-spacing: .5pt;"> </span>
            - <span style="letter-spacing: .5pt;"> </span>
            INDIVIDUAL <span style="letter-spacing: .5pt;"> </span>
            <span style="letter-spacing: -.1pt;">INDEX</span>
        </div>

        <table class="table table-bordered" style="border: 1px solid #000000; margin-bottom: 5px;">
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
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing the rate/amount of brokerage and other charges
                        levied on the client for trading on the stock exchange(s).</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">3</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Schedule of DP Charges and Option for DIS Booklet</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Charges for Depository Services / Demat charges and option for
                        issuance of DIS booklet.</td>
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
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing the rights and obligations of the account
                        holder.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">29</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">31</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">8</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Risk Disclosure Document (RDD)</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing risks associated with dealing in the securities
                        market.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">11</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">13</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">9</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Guidance Note</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing do's and don'ts for trading on exchange, for the
                        education of the investors.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">14</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">15</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">10</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Policies and Procedures</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document describing significant policies and procedures of the
                        stock broker.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">16</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">18</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">11</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Rights and Obligations of DP</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Rights and Obligations of Beneficial Owner and Depository
                        Participant as prescribed by SEBI and Depositories.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">34</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">35</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">12</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Intimation of Money Laundering</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">This document is to make the client aware of Anti Money Laundering
                        (AML) provisions.</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">B</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">36</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">36</td>
                </tr>
                <tr style="border: 1px solid #000000;font-size: 11px;">
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">13</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Risk Disclosure Document, Rights and Obligations and Do's &amp; Don'ts (Commodities)</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Document detailing risks associated with dealing in Commodity
                        Market, Rights and Obligations and Do's &amp; Don'ts for trading on Commodity Exchange for the
                        education of the investor.</td>
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
                        This document allows the client to give authority to another person for placing / giving /
                        executing orders on his / her behalf.
                    </td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">A</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">19</td>
                </tr>
                <tr>
                    <td style="text-align: center;border: 1px solid #000000;font-size: 11px;">17</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">Authorization for running account / request letter</td>
                    <td style="text-align: justify;border: 1px solid #000000;font-size: 11px;">
                        Letter of authority / request to Ventura Securities Limited
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

        <table style="font-size:13px !important;">
            <tr style="border: 1px solid #000000;font-size: 11px;">
                <th style="border: 1px solid #000000;font-size: 11px;">Name of Stock Broker / Trading Member</th>
                <th style="border: 1px solid #000000;font-size: 11px;" colspan="3">VENTURA SECURITIES LIMITED</th>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">Single SEBI Regn. No.</th>
                <td style="border: 1px solid #000000;font-size: 11px;">SEBI Registration INZ000194736</td>
                <th style="border: 1px solid #000000;font-size: 11px;">Regn. Date</th>
                <td style="border: 1px solid #000000;font-size: 11px;">August 21, 2018</td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">SEBI Regn. No. NSDL</th>
                <td style="border: 1px solid #000000;font-size: 11px;" colspan="3">IN-DP-565-2021 DP ID-IN303116</td>
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

                </td>
            </tr>
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;">Registered / Correspondence Office</th>
                <td style="border: 1px solid #000000;font-size: 11px;" colspan="3">
                    I-Think Techno Campus, "B" Wing, 8th Floor, Off. Pokhran Road No. 2,<br />
                    Close to Eastern Express Highway, Thane (West) - 400607, Maharashtra, India.
                </td>
            </tr>
        </table>

        <table style="font-size:13px !important; margin-top: 5px;">
            <tr>
                <th style="border: 1px solid #000000;font-size: 11px;"><span class="bold">Phone No.</span></th>
                <td style="border: 1px solid #000000;font-size: 11px;">91-22-6754 7000</td>
                <td style="border: 1px solid #000000;font-size: 11px;">91-22-6622 7100</td>
                <td style="border: 1px solid #000000;font-size: 11px;">
                    <span class="bold">Website:</span>
                    <a href="http://www.venturasecurities.com/">www.venturasecurities.com</a>
                </td>
            </tr>
        </table>

        <table style="font-size:13px !important;margin-top: 5px;">
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
                    <td style="border: 1px solid #000000;font-size: 11px;">Mr. D. P. Singh</td>
                    <td style="border: 1px solid #000000;font-size: 11px;">Mr. Hemant Majethia</td>
                    <td style="border: 1px solid #000000;font-size: 11px;">91-22-6754 7000</td>
                    <td style="border: 1px solid #000000;font-size: 11px;"><a href="mailto:compliance@ventura1.com">compliance@ventura1.com</a></td>
                </tr>
            </tbody>
        </table>


        <p style="font-size: 11px;">
            <span style="letter-spacing: -0.3pt;">
                For any grievance / dispute please contact us at the above address or email id -
            </span>
            <a href="mailto:complaints@ventura1.com">
                complaints@ventura1.com
            </a>
            <span style="letter-spacing: -0.3pt;">
                and Phone no.
            </span>
            91-22-67547000. In case not satisfied with the response, please contact BSE at
            <a href="mailto:dis@bseindia.com">dis@bseindia.com</a>
            and phone no. 91-22-22728517, NSE at
            <a href="mailto:ignse@nse.co.in">ignse@nse.co.in</a>
            and phone no. 1800 266 0058 / 91-22-26598191, MCX at
            <a href="mailto:grievance@mcxindia.com">grievance@mcxindia.com</a>
            and phone no. 91-22-66494070 / 91-22-67318888 &amp; Option 0, NCDEX at
            <a href="mailto:ig@ncdex.com">ig@ncdex.com</a>
            and phone no. 1800 26 62339 / 91-22-66406789
        </p>

    </div>

    <!-- Third Page with Content -->
    <div class="WordSection3">
        <div>
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 50px; width: 200px;" />
        </div>

        <div class="row custom-mar-para-one">
            <p style="font-size: 11px;padding-top: 0;padding-bottom: 0;padding-right: 10px;" class="instructions-header">INSTRUCTIONS/CHECK LIST FOR FILLING KYC FORM</p>

            <p style="font-size: 11px;" class="list-paragraph">A. IMPORTANT POINTS:</p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                1. Self-attested copy of PAN card is mandatory for all clients,
                including Promoters/Partners/Karta/Trustees and whole-time
                directors and persons authorized to deal in securities on
                behalf of company/firm/others.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                2. Copies of all the documents submitted by the applicant should
                be self-attested and accompanied by originals for verification.
                In case the original of any document is not produced for
                verification, then the copies should be properly attested by
                entities authorized for attesting the documents, as per the
                below-mentioned list.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                3. If any proof of identity or address is in a foreign
                language / regional language, then translation into
                English is required.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                4. Name &amp; address of the applicant mentioned on the KYC form, should
                match with the documentary proof submitted.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                5. If correspondence &amp; permanent address are different, then proofs for both have to be submitted.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                6. Sole proprietor must make the application in his individual name &amp; capacity.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                7. For non-residents and foreign nationals, (allowed to trade subject to RBI
                and FEMA guidelines), copy of passport/PIO Card/ OCI Card and overseas
                address proof is mandatory.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                8. For foreign entities, CIN is optional; and in the absence of DIN no. for
                the directors, their passport copy should be given.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                9. In case of Merchant Navy NRI's, Mariner's declaration or certified copy of
                CDC (Continuous Discharge Certificate) is to be submitted.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                10. For opening an account with Depository participant or Mutual Fund, for a
                minor, photocopy of the School Leaving Certificate / Mark sheet issued by
                Higher Secondary Board / Passport of Minor / Birth Certificate must be
                provided.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                11. Politically Exposed Persons (PEP) are defined as individuals who are or
                have been entrusted with prominent public functions in a foreign country,
                e.g., Heads of States or of Governments, senior politicians, senior
                Government / judicial / military officers, senior executives of state
                owned corporations, important political party officials, etc.
            </p>
        </div>

        <div class="row custom-mar-para-one">
            <p style="font-size: 11px;" class="list-paragraph">B. Proof of Identity (POI) : - List of documents admissible as Proof of Identity:
            </p>

            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                1. Unique Identification Number (UID) (Aadhaar)/ Passport/ Voter ID card/ Driving license.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                2. PAN card with photograph.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                3. Identity card / document with applicant's Photo, issued by any of the following :
                Central / State Government and its Departments, Statutory / Regulatory Authorities,
                Public Sector Undertakings, Scheduled Commercial Banks, Public Financial Institutions,
                Colleges affiliated to Universities, Professional Bodies such as ICAI, ICWAI, ICSI, Bar
                Council etc., to their Members; and Credit cards / Debit cards issued by Banks.
            </p>
        </div>

        <div class="row custom-mar-para-one">
            <p style="font-size: 11px;" class="list-paragraph">C. Proof of Address : - List of documents admissible as Proof of Address :</p>
            <p style="text-align: justify; font-weight: normal;font-size: 11px;">
                (*Documents having an expiry date should be valid on the date of submission.)
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                1. Passport / Voters Identity Card / Ration Card / Registered Lease or Sale Agreement
                of Residence / Driving License / Flat Maintenance bill / Insurance Copy /
                Aadhaar Card.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                2. Utility bills like Telephone Bill (only land line), Electricity bill or Gas
                bill - Not more than 3 months old.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                3. Bank Account Statement / Passbook - Not more than 3 months old.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                4. Self-declaration by High Court and Supreme Court judges, giving the new address
                in respect of their own accounts.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                5. Proof of address issued by any of the following: Bank Managers of Scheduled Commercial
                Banks / Scheduled Co-Operative Bank / Multinational Foreign Banks / Gazetted Officer
                / Notary public / Elected representatives to the Legislative Assembly / Parliament
                / Documents issued by any Govt. or Statutory Authority.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                6. Identity card / document with address, issued by any of the following: Central / State Government
                and its Departments, Statutory / Regulatory Authorities, Public Sector Undertakings, Scheduled
                Commercial Banks, Public Financial Institutions, Colleges affiliated to Universities and
                Professional Bodies such as ICAI, ICWAI, ICSI, Bar Council etc., to their Members.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                7. For FII / sub account, Power of Attorney given by FII / sub-account to the Custodians
                (which are duly notarized and/or apostiled or consularised) that gives the registered
                address should be taken.
            </p>
        </div>

        <div class="row custom-mar-para-one">
            <p style="font-size: 11px;" class="list-paragraph">D. Exemptions/clarifications to PAN :</p>
            <p style="text-align: justify; font-weight: normal;font-size:11px;">
                (*Sufficient documentary evidence in support of such claims to be collected.)
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                1. In case of transactions undertaken on behalf of Central Government and / or
                State Government and by officials appointed by Courts e.g. Official liquidator,
                Court receiver etc.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                2. Investors residing in the state of Sikkim.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                3. UN entities/multilateral agencies exempt from paying taxes / filing tax returns
                in India.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                4. SIP of Mutual Funds upto Rs 50,000 /- p.a.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                5. In case of institutional clients, namely, FIIs, MFs, VCFs, FVCIs,
                Scheduled Commercial Banks, Multilateral and Bilateral Development Financial Institutions,
                State Industrial Development Corporations, Insurance Companies registered with IRDA and
                Public Financial Institution as defined under section 4A of the Companies Act, 1956,
                Custodians shall verify the PAN card details with the original PAN card and provide duly
                certified copies of such verified PAN details to the intermediary.
            </p>
        </div>

        <div class="row custom-mar-para-one">
            <p style="font-size: 11px; margin-top: 20px;" class="list-paragraph">E. List of people authorized to attest the documents :</p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                1. Notary Public, Gazetted Officer, Manager of a Scheduled Commercial /
                Co-operative Bank or Multinational Foreign Banks (Name, Designation
                &amp; Seal should be affixed on the copy).
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                2. In case of NRIs, authorized officials of overseas branches of Scheduled Commercial Banks
                registered in India, Notary Public, Court Magistrate, Judge, Indian Embassy /
                Consulate General in the country where the client resides are permitted to attest the
                documents.
            </p>
        </div>

        <p style="font-size: 11px; font-weight: bold;padding-left: 10px;margin-bottom: 0;">
F. Additional documents in case of trading in derivatives segments - illustrative list :
</p>

<table style="width: 100%; padding-left: 10px;">
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

<p style="font-size: 11px; text-align: justify; margin-top: 5px;padding-left: 10px;margin-bottom: 5px;">
  *In respect of other clients, documents as per risk management policy of the stock broker need to be provided by the client from time to time.
</p>


<p style="font-size: 11px; font-weight: bold;padding-left: 10px;margin-bottom: 5px;">
G. Copy of cancelled cheque leaf / pass book / bank statement specifying name of the constituent, MICR Code and IFSC Code of the bank should be submitted.
</p>
<p style="font-size: 11px; font-weight: bold;padding-left: 10px;margin-bottom: 5px;">
H. Demat master or recent holding statement issued by DP bearing name of the client.
</p>

 <p style="font-size: 11px; font-weight: bold; margin-bottom:0 !important;padding-left: 10px;margin-bottom: 5px;">
I. For individuals :
</p>

        <div class="row" style="margin:0!important;padding-left: 10px;">

            <p style="margin-left: 0;margin-bottom: 5px; text-align: justify; font-weight: normal; font-size:11px; ">
                a. Stock broker has an option of doing 'in-person' verification through web camera at the
                branch office of the stock broker /sub- broker's office.
            </p>
            <p style="margin-left: 0; text-align: justify; font-weight: normal; font-size:11px;">
                b. In case of non-resident clients, employees at the stock broker's local office, overseas
                can do in-person' verification. Further, considering the infeasibility of carrying out
                'In-person' verification of the non-resident clients by the stock broker's staff,
                attestation of KYC documents by Notary Public, Court, Magistrate, Judge, Local Banker,
                Indian Embassy / Consulate General in the country where the client resides may be permitted.
            </p>
        </div>

    </div>

    <!-- First Holder Form Start -->
    <!-- Fifth Page with Content -->
    <div class="WordSection5">
        <table style="border-collapse: collapse; border-color: #f7efef; width: 30%; float: right;margin-top: 20px;">
            <tr>
                <td style="padding: 10px; text-align: left; font-weight: bold; border: 1px solid #000000;">
                    <span>FU</span> | ${data.clientCode!''}
                </td>
            </tr>
        </table>
        <br /><br />

        <!-- Instructions Section -->
        <table style="width: 100%; font-size: 11px; font-weight: normal !important; border: none; border-collapse: collapse; margin-top: 20px;">
            <thead>
                <tr style="border: none;">
                    <th style="text-align: left; padding-bottom: 10px; background: #A9A9A9; border: none;" colspan="2">
                        CENTRAL KYC REGISTRY | Know Your Customer (KYC) Application Form | Individual
                    </th>

                    <th style="text-align: right;  padding-bottom: 10px; background: #A9A9A9; border: none;">
                        FIRST HOLDER
                    </th>
                </tr>
                <tr style="border: none;">
                    <th style="text-align: left; background: #fff; border: none;" colspan="3">
                        Important Instructions :
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr style="border: none;">
                    <!-- Section 1 -->
                    <td style="vertical-align: top; width: 35% !important; border: none;">
                        <ul style="list-style-type: none; padding-left: 0; border: none;">
                            <span style="font-size: 10px; border: none;">(A) Fields marked with "*" are mandatory fields.</span><br />
                            <span style="font-size: 10px; border: none;">(B) Please fill the form in English and in BLOCK letters.</span><br />
                            <span style="font-size: 10px; border: none;">(C) Please fill the date in DD-MM-YYYY format.</span><br />
                            <span style="font-size: 10px; border: none;">(D) Please read section-wise detailed guidelines / instructions at the end.</span>
                        </ul>
                    </td>
                    <!-- Section 2 -->
                    <td style="vertical-align: top; width: 60%; border: none;">
                        <ul style="list-style-type: none; padding-left: 0; font-style: normal; border: none;">
                            <span style="font-size: 10px; border: none;">(E) List of State/U.T codes as per Indian Motor Vehicle Act, 1988 is available at the end.</span><br />
                            <span style="font-size: 10px; border: none;">(F) List of two-character ISO 3166 country codes is available at the end.</span><br />
                            <span style="font-size: 10px; border: none;">(G) KYC number of applicant is mandatory for update application.</span><br />
                            <span style="font-size: 10px; border: none;">(H) For particular section update, please tick (&#10003;) in the box available before the section
                                number and strike off the sections not required to be updated.</span>
                        </ul>
                    </td>
                    <!-- Section 3 -->
                    <td style="vertical-align: top; padding: 10px; width: 5%; border: none;">
                        <ul style="list-style-type: none; padding-left: 0; border: none;">
                            <img src="data:image/png;base64,${data.centralKycLogoBase64!''}" alt="Central KYC Logo" />
                        </ul>
                    </td>
                </tr>
            </tbody>
        </table>

        <table style="width: 100%; font-size: 13px; font-weight: normal !important; border: none;">
            <thead style="background-color: #757879 !important; border: none;">

                <tr style="border: none;">
                    <th colspan="5" style="border: none;">
                        <span style="font-size: 14px; padding-right: 50px; border: none;">For office use only</span>

                        <span style="font-weight: 400; border: none;">
                            Application Type <span style="color: red;">*</span>
                        </span>

                        <!-- Box with Checkmark -->
                        <span class="square-box" style=" border: none;width: 12px; height: 12px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-top:5px;">
                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                        </span>
                        <span style=" border: none; font-size: 11px; font-weight: 400;"> New</span>

                        <!-- Empty Box -->
                        <span class="square-box" style=" border: none; width: 12px; height: 12px; border: 1px solid #000; display: inline-block; text-align: center; margin-left: 10px; vertical-align: middle; margin-top:5px;">
                        </span>
                        <span style=" border: none; font-size: 11px; font-weight: 400;"> Update</span>
                    </th>
                </tr>


                <tr style="border: none;">
                    <th colspan="5" style="border: none;">
                        <span style=" border: none; font-size: 11px; font-weight: 300;font-style: italic;">(To be filled by financial institution)</span>
                        <span style=" border: none; font-weight: 400;">KYC Number <span style="display: inline-block; width: 100px; border-bottom: 1px solid #000; height: 10px;">  </span> </span>

                            <div class="square-box" style=" border: none; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 10.5px;font-size: 10px;">

                            </div>

                        <span style=" border: none; font-size: 11px; font-weight: 300;font-style: italic;">(Mandatory for KYC update request)</span>
                    </th>
                </tr>

<tr style="border: none;">
<th colspan="5" style="border: none;">

<span style="font-weight:400;padding-left:180px;">
Account Type <span style="color:red;">*</span>
</span>

<!-- Normal -->
<span class="square-box" style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;margin-left:10px;">
<#if !(data.typeOfAccount!'')?has_content || (data.typeOfAccount!'') == '144'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
</#if>
</span>
<span style="font-size:11px;font-weight:400;"> Normal</span>

<!-- Simplified -->
<span class="square-box" style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;margin-left:10px;">
<#if (data.typeOfAccount!'') == '244'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
</#if>
</span>
<span style="font-size:11px;font-weight:400;"> Simplified (for low risk customer)</span>

<!-- Small -->
<span class="square-box" style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;margin-left:10px;">
<#if (data.typeOfAccount!'') == '344'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
</#if>
</span>
<span style="font-size:11px;font-weight:400;"> Small</span>

</th>
</tr>
            </thead>
        </table>



        <!-- First Name, Middle Name, Last Name Section -->
        <table style="font-weight: normal; border: none;">
            <thead style="background: #f8f8f8; border: none;">
                <tr style="border: none;">
                    <th colspan="5" style="background: #A9A9A9; border: none;">
                        <span class="square-box" style="width: 12px; height: 12px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle;">
                            <#if (data.firstName!'')?has_content>
                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                            </#if>
                        </span>
                        1. PERSONAL DETAILS
                        <span style="font-size: 11px; font-weight: 400;">
                            (Please refer to instruction A at the end)
                        </span>
                    </th>
                </tr>
            </thead>
            <thead style="border: none; ">
                <tr style="border: none; ">
                    <th style="background: #fff; border: none; "></th>
                    <th style="background: #fff; border: none; width: 5%"><span class="label text-center" style="font-weight: 400; ">Prefix</span></th>
                    <th style="background: #fff; border: none;"><span class="label text-center" style="font-weight: 400; ">First Name</span></th>
                    <th style="background: #fff; border: none;"><span class="label text-center" style="font-weight: 400; ">Middle Name</span></th>
                    <th style="background: #fff; border: none;"><span class="label text-center" style="font-weight: 400; ">Last Name</span></th>
                </tr>
            </thead>
            <tbody style="border: none;">
                <tr style="border: none; ">
                    <td style="font-size: 11px; font-weight: 400; width: 25%; border:none;"><span class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; "><#if (data.firstName!'')?has_content><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" /></#if></span> Name (Same as ID proof)</td>

                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400; border-bottom: 1px solid #000000;  min-height:20px;"><strong>${data.namePrefix!''}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400; border-bottom: 1px solid #000000;  min-height:20px;"><strong>${(data.firstName!'')?upper_case}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400; border-bottom: 1px solid #000000;  min-height:20px;"><strong><#if (data.middleName!'')?has_content>${(data.middleName!'')?upper_case}<#else><span style="visibility: hidden;">.</span></#if></strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400; border-bottom: 1px solid #000000;  min-height:20px;"><strong>${(data.lastName!'')?upper_case}</strong></div></td>
                </tr>

<#if (data.gender!'')?lower_case == 'female' &&
     (data.maritalStatus!'') == '2' &&
     (data.maidenName!'')?has_content &&
     !['mr','mrs','ms','miss','na','n/a','null','-','']?seq_contains((data.maidenName!'')?trim?lower_case)>

<tr style="border: none;">
    <td style="font-size: 11px; font-weight: 400;width: 25%; border:none;">
        Maiden Name (if any <span style="color: red;">*</span>)
    </td>

    <!-- PREFIX -->
    <td style="border: none;">
        <div style="border-bottom: 1px solid #000; min-height:20px;">
            <strong>${data.maidenTitle!''}</strong>
        </div>
    </td>

    <!-- FIRST -->
    <td style="border: none;">
        <div style="border-bottom: 1px solid #000; min-height:20px;">
            <strong>${(data.maidenName!'')?upper_case}</strong>
        </div>
    </td>

    <!-- MIDDLE -->
    <td style="border: none;">
        <div style="border-bottom: 1px solid #000; min-height:20px;">
            <strong>${(data.maidenMiddleName!'')?upper_case}</strong>
        </div>
    </td>

    <!-- LAST -->
    <td style="border: none;">
        <div style="border-bottom: 1px solid #000; min-height:20px;">
            <strong>${(data.maidenLastName!'')?upper_case}</strong>
        </div>
    </td>
</tr>

</#if>

                <tr style="border: none;">
                    <td style="font-size: 11px; font-weight: 400;width: 25%; border:none; ">Father / Spouse Name <span style="color: red;">*</span></td>

                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${data.fatherNameTitle!''}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${(data.fathersFirstName!'')?upper_case}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${(data.fathersMiddleName!'')?upper_case}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${(data.fathersLastName!'')?upper_case}</strong></div></td>
                </tr>

                <tr style="border: none; ">
                    <td style="font-size: 11px; font-weight: 400;width: 25%; border:none;  ">Mother Name <span style="color: red;">*</span></td>

                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${data.motherNameTitle!''}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${(data.motherFirstName!'')?upper_case}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${(data.motherMiddleName!'')?upper_case}</strong></div></td>
                    <td style="border: none;"><div style="font-size: 11px; font-weight: 400;border-bottom: 1px solid #000000; min-height:20px;"><strong>${(data.motherLastName!'')?upper_case}</strong></div></td>
                </tr>
            </tbody>
        </table>



        <table style="font-weight: normal; border: none;">
            <tbody style="border: none;">
                <tr style="border: none;">
                    <td style="width: 25%;font-size: 11px; font-weight: 400; border: none;">Date of Birth <span style="color: red;">*</span></td>

                    <td style="border: none;">
                        <div style="width: 36%;border-bottom: 1px solid #000;">
                            <#if (data.dateOfBirth!'')?has_content>
                            <#list 0..<(data.dateOfBirth!'')?length as i>
                            <div style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 10.5px; text-transform: uppercase; font-size: 10px;">
                                <strong>${(data.dateOfBirth!'')[i..i]}</strong>
                            </div>
                            </#list>
                            </#if>
                        </div>
                    </td>

                </tr>
            </tbody>
        </table>

        <!-- Gender, Marital Status, and Occupation -->
        <table style="font-weight: normal; border: none;">
            <tbody style="border: none;">
                <!-- First Row -->
                <tr style="border: none;">
                    <td style="width: 25%; vertical-align: middle;font-size: 11px; font-weight: 400; border: none;">Gender <span style="color: red;">*</span></td>
                    <td style="width: 60%; vertical-align: middle;  border: none; ">
                        <table style="border: none !important;">
                            <tr style="border: none;">
                                <td style=" padding: 0 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.gender!'') == 'Male'>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">M - Male</span>
                                </td>
                                <td style=" padding: 0 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.gender!'') == 'Female'>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">F - Female</span>
                                </td>
                                <td style=" padding: 0 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.gender!'') == 'Transgender'>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">T - Transgender</span>
                                </td>
                            </tr>
                        </table>
                    </td>


                    <td rowspan="4" style="padding: 5px; width: 15%; text-align: center; border: none; vertical-align: middle;">

                        <!-- Profile Image -->
                        <#if (data.profilePicBase64!'')?has_content>
                            <img src="data:image/jpeg;base64,${data.profilePicBase64!''}" alt="User Profile" height="140" width="120" /><br /><br />
                        <#else>
                            <img src="data:image/png;base64,${data.defaultProfilePicBase64!''}" alt="Default Profile" height="140" width="120" /><br /><br />
                        </#if>


                        <!-- Signature Image -->
                        <#if (data.signaturePicBase64!'')?has_content>
                            <img src="data:image/jpeg;base64,${data.signaturePicBase64!''}" alt="Signature" height="30" width="120" />
                        <#else>
                            <img src="data:image/png;base64,${data.defaultSignaturePicBase64!''}" alt="Default Signature" height="30" width="120" />
                        </#if>

                    </td>


                </tr>

                <!-- Second Row -->
                <tr style="border: none;">
                    <td style="width: 20%; vertical-align: middle;font-size: 11px; font-weight: 400; border: none;">
                        Marital Status <span style="color: Red;">*</span>
                    </td>
                    <td style="width: 80%; border: none; vertical-align: middle;">
                         <table style="border: none;">
                            <tr style="border: none;">
                                <td style=" padding: 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.maritalStatus!'') == '1'>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">Single</span>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.maritalStatus!'') == '2'>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">Married</span>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.maritalStatus!'') == '3' || (data.maritalStatus!'') == '4' || (data.maritalStatus!'') == '5'>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div>
                                    <span style="font-size: 11px; font-weight: 400;">Others</span>
                                </td>
                            </tr>


                        </table>
                    </td>
                </tr>

                <!-- Third Row -->
                <tr style="border: none;">
                    <td style="width: 22%; vertical-align: middle;font-size: 11px; font-weight: 400; border: none;">
                        Citizenship <span style="color: red;">*</span>
                    </td>
                    <td style="width: 78%; padding: 5px; border: none; vertical-align: middle;">

                        <table style="border: none; ">
                            <tr style="border: none;">
                                <td style=" padding: 2px; border: none; vertical-align: middle;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.citizenshipIsoCode!'') == 'IN'>
                                        <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                                        </#if>
                                    </div>
                                    <span style="font-size: 11px; font-weight: 400;">IN - Indian</span>
                                </td>

                                <td style=" padding: 2px; border: none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.citizenshipIsoCode!'')?has_content && (data.citizenshipIsoCode!'') != 'IN'>
                                        <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                                        </#if>
                                    </div>
                                    <span style="font-size: 11px; font-weight: 400;">
                                        Others (ISO 3166 Country Code:
                                        <span  style="  width: 20px; height:20px; border-bottom : 1px solid #000; padding: 5px; vertical-align: top; ">
                                            <strong>${data.citizenshipIsoCode!''}</strong>
                                        </span>
                                    </span>
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>

                <!-- Fourth Row -->
                <tr style="border: none;">
                    <td style="width: 22%; vertical-align: top; font-size: 11px; font-weight: 400; border: none;">
                        Residential Status <span style="color: red;">*</span>
                    </td>

                    <td style="width: 78%; padding: 5px; border: none; vertical-align: middle;">
                        <table style="border: none; ">
                            <tr style="border: none; vertical-align: bottom;">
                                <td style=" padding: 2px; border: none; vertical-align: bottom;">
                                    <label style="display: flex; align-items: end; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000;">
                                            <#if (data.investorTypeId!'') == '1'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div>
                                        <span style="font-size: 11px; font-weight: 400; ">Resident Individual</span>
                                    </label>
                                </td>

                                <td style=" padding: 2px; border: none; vertical-align: bottom;">
                                    <label style="display: flex; align-items: end; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000;">
                                            <#if (data.investorTypeId!'') == '4'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div>
                                        <span style="font-size: 11px; font-weight: 400;">Non-Resident Indian</span>
                                    </label>
                                </td>
                            </tr>

                            <tr  style="border: none; vertical-align: bottom;">
                                <td style=" padding: 2px; border: none; vertical-align: bottom;">
                                    <label style="display: flex; align-items: end; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000;">
                                            <#if (data.investorTypeId!'') == '5'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div>
                                        <span style="font-size: 11px; font-weight: 400;">Foreign National</span>
                                    </label>
                                </td>

                                <td style=" padding: 2px; border: none; vertical-align: bottom;">
                                    <label style="display: flex; align-items: end; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000;">
                                            <#if (data.investorTypeId!'') == ''>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div>
                                        <span style="font-size: 11px; font-weight: 400;">Person of Indian Origin</span>
                                    </label>
                                </td>
                            </tr>
                        </table>
                    </td>

                </tr>


            </tbody>
        </table>
        <table style="font-weight: normal; border:none;">
            <tbody>
                <!-- Fifth Row -->
                <tr>
                    <td style="width: 20%; vertical-align: top;font-size: 11px; font-weight: 400; border: none;">
                        Occupation Type <span style="color: red;">*</span>
                    </td>
                    <td style="width: 80%;padding: 5px; border: none;">
                        <table style="border: none; border-collapse: collapse;">
                            <tr style="border: none;">
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap; ">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == ''>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">S - Service</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Salaried - Pvt Sector'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Private Sector</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Salaried - Public Sector'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Public Sector</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Salaried - GOVT Service'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Government Sector</span>
                                    </label>
                                </td>
                            </tr>

                            <tr>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Others'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">O - Others</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Professional'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Professional</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            &nbsp;
                                        </div> <span style="font-size: 11px; font-weight: 400;">Self Employed</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Retired'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Retried</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Housewife'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Housewife</span>
                                    </label>
                                </td>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Student'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">Student</span>
                                    </label>
                                </td>
                            </tr>
                            <tr>
                                <td style=" padding: 2px; border: none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            <#if (data.occupationType!'') == 'Business'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                            <#else>
                                                &nbsp;
                                            </#if>
                                        </div> <span style="font-size: 11px; font-weight: 400;">B - Business</span>
                                    </label>
                                </td>
                            </tr>
                            <tr>
                                <td style="border: none; padding: 2px;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                            &nbsp;
                                        </div> <span style="font-size: 11px; font-weight: 400;">X-Not Categorised</span>
                                    </label>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>

        <div style="padding: 0 10px; font-family: Arial, sans-serif; width: fit-content; font-size:11px;">
            <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding: 5px;">
                <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; margin-right: 5px; vertical-align: middle;">
                    <#if (data.citizenshipIsoCode!'')?has_content>
                         <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                            alt="Checkmark"
                            style="position:absolute; top:0; left:0; width:10px; height:10px;" />
                    </#if>
                </span>

                2. TICK IF APPLICABLE
                <span style="font-size: 11px; font-weight: 400;">RESIDENCE FOR TAX PURPOSES IN JURISDICTION(S) OUTSIDE INDIA</span>
                <span style="font-size: 10px; font-weight: 400;">(Please refer to instruction B at the end)</span>
            </label>


            <div style="margin-top: 10px; font-size: 11px;">
                <label>ADDITIONAL DETAILS REQUIRED <span style="color: red;">*</span></label>
                <span style="font-size: 11px;">(Mandatory only if section 2 is ticked)</span>
            </div>


            <table style="border-collapse: collapse; margin-top: 10px; font-size: 13px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="padding: 5px; width: 70%; font-size:11px; border:none;">
                        ISO 3166 Country Code of Jurisdiction of Residence <span style="color: red;">*</span>
                    </td>
                    <td style="padding: 5px; border:none;">

                         <span  style=" border:none; width: 100px; height: 10px;  display: inline-block; font-size: 10px; text-align: center; line-height: 10px; line-height:2px; margin-right: 2px;">
                         <strong>${data.citizenshipIsoCode!''}</strong>
                         ________________________________

                            </span>

                    </td>
                </tr>

                <tr style="border:none;">
                    <td style="padding: 5px; width: 90%; font-size:14px; border:none;">
                        Tax Identification Number or equivalent
                        <span style="font-size: 11px;">(if issued by jurisdiction) <span style="color: red;">*</span></span>
                    </td>
                    <td style="padding: 5px; border:none;">
                        <#if (data.taxIdNumber!'')?has_content>
                        <#list 0..<(data.taxIdNumber!'')?upper_case?length as i>
                            <span style=" border:none; width: 10px; height: 30px; display: inline-block; font-size: 10px; text-align: center; line-height: 10px; margin-right: 5px;">
                                <strong>${((data.taxIdNumber!'')?upper_case)[i..i]}</strong>
                                  ____________
                            </span>
                        </#list>
                        </#if>
                    </td>
                </tr>

                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; font-size:11px; border:none;">
                        Place / City of Birth <span style="color: red;">*</span>

                        <span style="border:none; text-decoration:underline; width:350px; text-underline-offset:5px;">
                            <strong>${(data.cityOfBirth!'')?upper_case}</strong>
                        </span>

                    </td>

                     <td style="padding: 5px; width: 50%; font-size:11px; border:none;">
                        ISO 3166 Country Code of Birth <span style="color: red;">*</span>
                       <span style=" border:none; text-decoration:underline ; width:350px; text-underline-offset: 5px;"><strong>${data.countryOfBirthName!''}</strong>
                       </span>

                    </td>

                </tr>
            </table>
<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;"> -------- 01 -------- </span>
        <!-- Page Break -->
        <div class="page-break"></div>
        </div>
        <br />

        <div style="padding: 10px; font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">

            <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding: 5px !important;">
                <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                    <#if (data.passportNumber!'')?has_content ||
                         (data.passportValidUpto!'')?has_content ||
                         (data.voterIdNumber!'')?has_content ||
                         (data.taxPanNo!'')?has_content ||
                         (data.drivingLicenseNumber!'')?has_content ||
                         (data.drivingLicenseExpiry!'')?has_content ||
                         (data.aadhaarNumber!'')?has_content ||
                         (data.otherIdNumber!'')?has_content ||
                         (data.identificationNumber!'')?has_content>
                         <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                            alt="Checkmark"
                            style="position:absolute; top:0; left:0; width:10px; height:10px;" />
                    </#if>
                </span>
                3 . PROOF OF IDENTITY (POI) <span style="font-size: 11px; font-weight: 400;">(Please refer instruction C at the end)</span>
            </label>


            <div style="margin-top: 5px; font-size: 11px;">
                <span style="font-size: 11px;font-style: italic;">
                    (Certified copy of any one of the following Proof of Identity[PoI] needs to be submitted) <br /><br />
                </span>
            </div>
            <table style="border-collapse: collapse; margin-top: 0; font-size: 13px; width: 100%; border:none; border:none;">
                <tr style="border:none;">
                    <td style="padding: 0; width: 50%; border:none;">
                        <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                            <#if (data.passportNumber!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                     alt="Checkmark"
                                     style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                            </#if>
                        </span>
                        <span style="font-size: 11px;">A. Passport Number : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 100px;">
                            <strong><#if (data.passportNumber!'')?has_content>${(data.passportNumber!'')?upper_case}</#if></strong>
                        </span>
                    </td>


                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;">Passport Expiry Date : -<span style="border-bottom:1px solid #000;"><strong> ${data.passportValidUpto!''}</strong></span></span>

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                            <#if (data.voterIdNumber!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                     alt="Checkmark"
                                     style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                            </#if>
                        </span>
                        <span style="font-size: 11px;">B. Voter ID Card : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 100px;">
                            ${data.voterIdNumber!' '}
                        </span>
                    </td>

                    <td style="padding: 5px; border:none;">

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                            <#if (data.taxPanNo!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                     alt="Checkmark"
                                     style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                            </#if>
                        </span>
                        <span style="font-size: 11px;">C. PAN Card : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 100px;">
                            <strong>${(data.taxPanNo!' ')?upper_case}</strong>
                        </span>

                    </td>

                    <td style="padding: 5px; width: 30%; border:none;">

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                            <#if (data.drivingLicenseNumber!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                     alt="Checkmark"
                                     style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                            </#if>
                        </span>
                        <span style="font-size: 11px;">D. Driving Licence : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 100px;">
                            ${data.drivingLicenseNumber!' '}
                        </span>
                    </td>

                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;">Driving Licence Expiry Date : -</span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 80px; margin-left:150px;">
                           ${data.drivingLicenseExpiry!' '}
                        </span>
                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="border:none;">
                        <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                            <#if (data.aadhaarNumber!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                     alt="Checkmark"
                                     style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                            </#if>
                        </span>
                        <span style="font-size: 11px;">E. UID (Aadhaar) : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 100px;">
                            ${data.aadhaarNumber!' '}
                        </span>
                    </td>

                    <td style="padding: 5px; width: 30%; border:none;">

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="border:none;">
                        <span class="square-box" style="position: relative; width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; vertical-align: middle; margin-right: 5px;">
                            <#if (data.otherIdNumber!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                     alt="Checkmark"
                                     style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                            </#if>
                        </span>
                        <span style="font-size: 11px;">Z. Others (any document notified by central government) : -  </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 25px;">
                            ${data.otherIdNumber!' '}
                        </span>
                    </td>

                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;">Identification Number : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 100px;">
                            ${data.identificationNumber!' '}
                        </span>
                    </td>

                </tr>
                <tr style="border:none;">
                    <td style="border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                        <span style="font-size: 11px;">S. Simplified Measures Account - Document Type code : - </span>
                        <span style="border-bottom: 1px solid #000; display: inline-block; min-width: 25px;">
                            ${data.simplifiedMeasuresAddressCode!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;">Identification Number : - </span>
                        _____________
                    </td>
                </tr>
            </table>
        </div>

        <div style="padding: 10px; font-family: Arial, sans-serif; width: fit-content; font-size:11px;">
            <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px !important;">
                4 . PROOF OF ADDRESS (POA) <span style="color: red;">*</span>
            </label>
            <label style="font-weight: 400; display: block; margin-bottom: 5px; font-size: 11px; background-color: #F5F5DC; padding:5px !important; position: relative;">


                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position: relative;">
                    <#if (data.permAddress1!'')?has_content>
                        <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                             alt="Checkmark"
                             style="position: absolute; top: 0; left: 0; width: 10px; height: 10px;" />
                    </#if>
                </div>


                4.1 CURRENT / PERMANENT / OVERSEAS ADDRESS DETAILS
                <span style="font-size: 10px;">(Please see instruction D at the end)</span>
            </label>

            <div style="margin-top: 5px; margin-bottom: 10px;">
                <span style="font-size: 11px; font-style: italic;">
                    (Certified copy of any one of the following Proof of Address[PoA] needs to be submitted)
                </span>
            </div>

            <!-- Add a Address Type details -->
            <table style="font-weight: normal; margin-bottom: 10px; border:none;">
                <tbody style="border:none;">
                    <tr style="border:none;">
                        <td style="width: 20%; border:none;">
                            <span style="font-size: 11px; font-weight: 400;">Address Type</span><span style="color: red;">*</span>
                        </td>
                        <td style="width: 80%; padding: 5px; border:none;">
                            <table style="border:none;">
                              <tr style="border:none;">
                                    <!-- Residential / Business -->
                                    <td style="border:none; padding: 2px;">
                                        <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; position: relative;">
                                                <#if (data.addressType!'') == 'residential_business'>
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                                         alt="Checkmark" style="position: absolute; top: 0; left: 0; height: 10px; width: 10px;" />
                                                </#if>
                                            </div>
                                            <span style="font-size: 11px; font-weight: 400;">Residential / Business</span>
                                        </label>
                                    </td>

                                    <!-- Residential -->
                                    <td style="border:none; padding: 2px;">
                                        <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; position: relative;">
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                                         alt="Checkmark" style="position: absolute; top: 0; left: 0; height: 10px; width: 10px;" />
                                            </div>
                                            <span style="font-size: 11px; font-weight: 400;">Residential</span>
                                        </label>
                                    </td>

                                    <!-- Business -->
                                    <td style="border:none; padding: 2px;">
                                        <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; position: relative;">
                                                <#if (data.addressType!'') == 'business'>
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                                         alt="Checkmark" style="position: absolute; top: 0; left: 0; height: 10px; width: 10px;" />
                                                </#if>
                                            </div>
                                            <span style="font-size: 11px; font-weight: 400;">Business</span>
                                        </label>
                                    </td>

                                    <!-- Registered Office -->
                                    <td style="border:none; padding: 2px;">
                                        <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; position: relative;">
                                                <#if (data.addressType!'') == 'registered_office'>
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                                         alt="Checkmark" style="position: absolute; top: 0; left: 0; height: 10px; width: 10px;" />
                                                </#if>
                                            </div>
                                            <span style="font-size: 11px; font-weight: 400;">Registered Office</span>
                                        </label>
                                    </td>

                                    <!-- Unspecified -->
                                    <td style="border:none; padding: 2px;">
                                        <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; position: relative;">
                                                <#if (data.addressType!'') == 'unspecified'>
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}"
                                                         alt="Checkmark" style="position: absolute; top: 0; left: 0; height: 10px; width: 10px;" />
                                                </#if>
                                            </div>
                                            <span style="font-size: 11px; font-weight: 400;">Unspecified</span>
                                        </label>
                                    </td>
                                </tr>

                            </table>
                        </td>
                    </tr>

                <tr style="border:none;">
                    <td style="width: 20%; vertical-align: top; border:none;">
                        <span style="font-size: 11px; font-weight: 400;">Proof of Address</span>
                        <span style="color: red;">*</span>
                    </td>

                    <td style="width: 80%; padding: 5px; border:none;">
                        <table style="border:none;">
                            <tr style="border:none;">
                                <!-- Passport -->
                                <td style="border:none; padding: 2px;">
                                    <label style="display:flex; align-items:center; gap:5px; white-space:nowrap;">
                                        <div class="square-box" style="width:10px; height:10px; border:1px solid #000;">
                                            <#if (data.proofOfAddress!'')?upper_case == 'PASSPORT'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                                            </#if>
                                        </div>
                                        <span style="font-size:11px; font-weight:400;">Passport</span>
                                    </label>
                                </td>

                                <!-- Driving License -->
                                <td style="border:none; padding: 2px;">
                                    <label style="display:flex; align-items:center; gap:5px; white-space:nowrap;">
                                        <div class="square-box" style="width:10px; height:10px; border:1px solid #000;">
                                            <#if (data.proofOfAddress!'')?upper_case == 'DRIVING LICENSE' || (data.proofOfAddress!'')?upper_case == 'DRIVING_LICENSE'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                                            </#if>
                                        </div>
                                        <span style="font-size:11px; font-weight:400;">Driving License</span>
                                    </label>
                                </td>

                                <!-- UID (Aadhaar) -->
                                <td style="border:none; padding: 2px;">
                                    <label style="display:flex; align-items:center; gap:5px; white-space:nowrap;">
                                        <div class="square-box" style="width:10px; height:10px; border:1px solid #000;">
                                        </div>
                                        <span style="font-size:11px; font-weight:400;">UID (Aadhaar)</span>
                                    </label>
                                </td>
                            </tr>

                            <tr style="border:none;">
                                <!-- Voter Identity Card -->
                                <td style="border:none; padding: 2px;">
                                    <label style="display:flex; align-items:center; gap:5px; white-space:nowrap;">
                                        <div class="square-box" style="width:10px; height:10px; border:1px solid #000;">
                                        </div>
                                        <span style="font-size:11px; font-weight:400;">Voter Identity Card</span>
                                    </label>
                                </td>

                                <!-- NREGA Job Card -->
                                <td style="border:none; padding: 2px;">
                                    <label style="display:flex; align-items:center; gap:5px; white-space:nowrap;">
                                        <div class="square-box" style="width:10px; height:10px; border:1px solid #000;">
                                        </div>
                                        <span style="font-size:11px; font-weight:400;">NREGA Job Card</span>
                                    </label>
                                </td>

                                <!-- Others -->
                                <td style="border:none; padding: 2px;">
                                    <label style="display:flex; align-items:center; gap:5px; white-space:nowrap;">
                                        <div class="square-box" style="width:10px; height:10px; border:1px solid #000;">
                                            <#if (data.proofOfAddress!'')?upper_case != 'PASSPORT' && (data.proofOfAddress!'')?upper_case != 'DRIVING LICENSE' && (data.proofOfAddress!'')?upper_case != 'DRIVING_LICENSE'>
                                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
                                            </#if>
                                        </div>
                                        <span style="font-size:11px; font-weight:400;">
                                            Others : <#if (data.proofOfAddress!'')?has_content>${(data.proofOfAddress!'')?lower_case?cap_first}<#else>_____________</#if>
                                        </span>
                                    </label>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>


                            </table>

                            <table style="border:none;">
                                <tr style="border:none;">
                                    <!-- Simplified Measures -->
                                    <td style="border: transparent !important; padding: 2px; border:none;">
                                        <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                                <#if (data.proofOfAddress!'') == 'simplified_measures'>
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                                </#if>
                                            </div>
                                            <span style="font-size: 11px; font-weight: 400;">Simplified Measures</span> Account-Document Type code: <#if (data.simplifiedMeasuresAddressCode!'')?has_content>${data.simplifiedMeasuresAddressCode!''}<#else>_____________</#if>
                                        </label>
                                    </td>
                                </tr>
                            </table>

                </tbody>
            </table>



            <h2 style="font-size: 14px;">Address</h2>
            <div class="col-sm-12 p-0" style="font-size: 14px;">
                <span style="font-size: 11px; font-weight: 400;">Line 1* : </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 80%; border-bottom: 1px solid #000000;">
                    <strong>${(data.permAddress1!'')?upper_case}</strong>
                </span>
            </div>
            <div class="col-sm-12 p-0" style="font-size: 14px;">
                <span style="font-size: 11px; font-weight: 400;">Line 2 : </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 80%; border-bottom: 1px solid #000000;">
                    <strong>${(data.permAddress2!'')?upper_case}</strong>
                </span>
            </div>
            <div class="row col-sm-12 m-0 5 p-0" style="font-size: 14px;">
                <span style="font-size: 11px; font-weight: 400;">Line 3 : </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 50%; border-bottom: 1px solid #000000;">
                    <strong>${(data.permAddress3!'')?upper_case}</strong>
                </span>
                <span style="font-size: 11px; font-weight: 400;">City / Town / Village : </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                    <strong>${(data.permCity!'')?upper_case}</strong>
                </span>
            </div>

            <div class="row col-sm-12 p-0 m-0 5" style="font-size: 14px; ">
                <span style="font-size: 11px; font-weight: 400;">District * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                    <strong>${data.permCity!''}</strong>
                </span>&nbsp;

                <span style="font-size: 11px; font-weight: 400;">Pin / Post Code * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                    <strong>${data.permPinCode!''}</strong>
                </span>&nbsp;
                <span style="font-size: 11px; font-weight: 400;">State / U.T Code * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width:20%; border-bottom: 1px solid #000000;">
                    <strong>${(data.permState!'')?upper_case}</strong>
                </span>&nbsp;
            </div>

             <div class="row col-sm-12 p-0 m-0 5" style="font-size: 14px; ">
                <span style="font-size: 11px; font-weight: 400;">ISO 3166 Country Code * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                    <strong>${(data.permCountry!'')?upper_case}</strong>
                </span>
            </div>
        </div>

    <div style=" font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">
            <label style="font-weight: 400; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px !important; border:none;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    <!-- Show checkmark if address_line1 exists in DB -->
                                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />

                </div>

                <span style="font-size: 11px; font-weight: 400; border:none; ">4.2 CORRESPONDENCE / LOCAL ADDRESS DETAILS</span>
                <span style="font-size: 10px; font-weight: 400; border:none; ">(Please see instruction E at the end)</span>
            </label>


            <table style="border-collapse: collapse; margin-bottom: 5px; font-size: 11px; border:none;">
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <!-- Show checkmark if address_line1 exists in DB -->
                            <#if (data.permAddress1!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px; border:none;">Same as Current / Permanent / Overseas Address Details</span>
                        <span style="font-size: 10px; border:none;">(In case of multiple correspondence/local addresses please <b>fill 'Annexure A1'</b>)</span>
                    </td>
                </tr>
            </table>
            <div class="col-sm-12 p-0" style="font-size: 11px; height:20px; border:none; ">
                <span style="font-size: 11px; font-weight: 400;">Line 1* :</span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 90%; border-bottom: 1px solid #000000;">
                    <strong>${(data.corrAddress1!'')?upper_case}</strong>
                </span>
            </div>

            <div class="col-sm-12 p-0" style="font-size: 11px; height:20px; border:none;">
                <span style="font-size: 11px; font-weight: 400;">Line 2 :</span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 90%; border-bottom: 1px solid #000000;">
                    <strong>${(data.corrAddress2!'')?upper_case}</strong>
                </span>
            </div>

            <div class="row col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px; border:none;">
                <span style="font-size: 11px; font-weight: 400;">Line 3 :</span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 50%; border-bottom: 1px solid #000000;">
                    <strong>${(data.corrAddress3!'')?upper_case}</strong>
                </span>
                <span style="font-size: 11px; font-weight: 400;">City / Town / Village :</span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                    <strong>${(data.corrCity!'')?upper_case}</strong>
                </span>
            </div>

            <div class="row col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px; border:none;">
                <span style="font-size: 11px; font-weight: 400;">District * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 18%; border-bottom: 1px solid #000000;">
                    ${data.corrDistrict!''}
                </span>&nbsp;

                <span style="font-size: 11px; font-weight: 400;">Pin / Post Code * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 18%; border-bottom: 1px solid #000000;">
                    <strong>${data.corrPinCode!''}</strong>
                </span>&nbsp;
                <span style="font-size: 11px; font-weight: 400;">State / U.T Code * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width:18%; border-bottom: 1px solid #000000;">
                   <strong>${(data.corrState!'')?upper_case}</strong>
                </span>&nbsp;


            </div>

             <div class="row col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px; border:none;">
                <span style="font-size: 11px; font-weight: 400;">ISO 3166 Country Code * </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width:20%; border-bottom: 1px solid #000000;">
                    <strong>${(data.corrCountry!'')?upper_case}</strong>
                </span>

            </div>

        </div>

<div style=" font-family: Arial, sans-serif; width: fit-content; font-size:11px !important;border:none;margin-top: 20px;">
            <label style="font-weight: 400; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:15px !important;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">

                </div>
                <span style="font-size: 11px; font-weight: 400;">4.3 ADDRESS IN THE JURISDICTION DETAILS WHERE APPLICANT IS RESIDENT OUTSIDE INDIA FOR TAX PURPOSES *</span>
                <span style="font-size: 10px; font-weight: 400;">(Applicable if section 2 is ticked.)</span>
            </label>


            <table style="border-collapse: collapse; margin-bottom: 5px; font-size: 11px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                        </div>
                        <span style="font-size: 11px; border:none;">Same as Current / Permanent / Overseas Address Details</span>
                    </td>
                    <td style="padding: 5px; width: 50%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">

                        </div>
                        <span style="font-size: 11px; border:none;">Same as Correspondence / Local Address Details</span>
                    </td>
                </tr>

            </table>

            <div class="col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px; ">
                <span style="font-size: 11px; font-weight: 400;">Line 1* : </span>
                <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 90%; border-bottom: 1px solid #000000;">
                </span>
            </div>
            <div class="col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px;">
                <span style="font-size: 11px; font-weight: 400;">Line 2 : </span>
                 <span style="font-size: 11px; font-weight: 400; display: inline-block; width:90%; border-bottom: 1px solid #000000;">
                </span>
            </div>
            <div class="row col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px;">
                <span style="font-size: 11px; font-weight: 400;">Line 3 : </span>
                 <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 50%; border-bottom: 1px solid #000000;">
                </span>

                <span style="font-size: 11px; font-weight: 400;">City / Town / Village : </span>
                 <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 14%; border-bottom: 1px solid #000000;">
                </span>

            </div>
            <div class="row col-sm-12 p-0 m-0 5" style="font-size: 11px; height:20px;">
                <span style="font-size: 11px; font-weight: 400;">State * </span>
                 <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                </span> &nbsp; &nbsp; &nbsp;
                <span style="font-size: 11px; font-weight: 400;">Zip / Post Code * </span>
                 <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 12%; border-bottom: 1px solid #000000;">
                </span>&nbsp;
                <span style="font-size: 11px; font-weight: 400;">ISO 3166 Country Code * </span>
                 <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 8%; border-bottom: 1px solid #000000;">
                </span>
            </div>
        </div>


    </div>
    <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 02 -------- </span>
        <!-- Page Break -->
        <div class="page-break"></div>


    <!-- Sixth Page with Content -->
    <div style="border:none;" >

        <!-- commented out section 4.3 duplicate removed -->

        <div style=" font-family: Arial, sans-serif; width: fit-content; font-size:11px !important; border:none;margin-top: 20px;">
           <label style="border:none; font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px !important;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 10px; text-transform: uppercase;">
                                                   <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />

                </div>
                <span style="font-size: 11px; border:none;">5 . CONTACT DETAILS</span>
                <span style="font-size: 10px; border:none; font-weight: 400;">
                    (All Communications will be sent on provided Mobile No. / Email-Id) (Please refer instructions F at the end)
                </span>
            </label>


            <table style="border-collapse: collapse; margin-top: 5px;margin-bottom: 5px; font-size: 13px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="padding: 2px; width: 40%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">Tel. (Off)</span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 35%; border-bottom: 1px solid #000000;">
                        </span>
                    </td>
                    <td style="padding: 2px; width: 40%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">Tel. (Res)</span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 35%; border-bottom: 1px solid #000000;">
                        </span>
                    </td>
                    <td style="padding: 2px; width: 40%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">FAX</span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 35%; border-bottom: 1px solid #000000;">
                        </span>
                    </td>
                </tr>
            </table>
            <table style="border-collapse: collapse; font-size: 13px;margin-top: 5px;margin-bottom: 5px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="width: 40%; vertical-align: middle; border:none;">
                        Mobile <span style="color: Red; ">*</span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 40%; border-bottom: 1px solid #000000;">
                            <strong>+91 - ${data.mobile!''}</strong>
                        </span>
                    </td>
                    <td style="width: 60%; padding: 2px; border:none;">

                        <table  style="border:none;">
                            <tr style="border:none;">
                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                         <#if (data.mobile!'')?has_content>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px;">Self</span>
                                </td>

                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                        <#if (data.mobileBelongsTo!'')?contains('spouse')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px;">Spouse</span>
                                </td>

                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                        <#if (data.mobileBelongsTo!'')?contains('dependent_parent')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px;">Dependent Parent</span>
                                </td>

                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                        <#if (data.mobileBelongsTo!'')?contains('dependent_children')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px;">Dependent Children</span>
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>
            </table>
            <table style="border-collapse: collapse; font-size: 13px;margin-top: 5px;margin-bottom: 5px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="width: 40%; vertical-align: middle; border:none;">
                        Email Id <span style="color: Red;">*</span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 60%; border-bottom: 1px solid #000000; text-transform: uppercase;">
                            <strong>${data.email!''}</strong>
                        </span>
                    </td>
                    <td style="width: 60%; padding: 2px; border:none;">

                        <table style="border:none;">
                            <tr style="border:none;">
                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.mobile!'')?has_content>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">Self</span>
                                </td>

                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.emailBelongsTo!'')?lower_case?contains('spouse')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">Spouse</span>
                                </td>

                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.emailBelongsTo!'')?lower_case?contains('dependent parent')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">Dependent Parent</span>
                                </td>

                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.emailBelongsTo!'')?lower_case?contains('dependent children')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div> <span style="font-size: 11px; font-weight: 400;">Dependent Children</span>
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>
            </table>
        </div>



        <div style=" font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">
            <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px !important;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    <#if (data.additionRelatedPerson!'')?has_content || (data.deletionRelatedPerson!'')?has_content>
                        <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                    </#if>
                </div>
                <span style="font-size: 11px;">6. DETAILS OF RELATED PERSON</span>
                <span style="font-size: 10px;font-weight: 400;">(In case of additional related persons, please <b>fill 'Annexure A1'</b>) (please refer instructions G at the end)</span>
            </label>


            <table style="border-collapse: collapse; margin-top: 3px;  background-color: #eeeeee; font-size: 11px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="padding: 5px; width: 25%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <#if (data.additionRelatedPerson!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">Addition of Related Person</span>
                    </td>

                    <td style="padding: 5px; width: 75%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <#if (data.deletionRelatedPerson!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">Deletion of Related Person</span>
                        <span style="font-size: 11px;font-weight: 400;padding-left: 20px; padding-right: 5px;">KYC Number of Related Person (if available*)</span>

                       ____________
                         </td>
                </tr>
            </table>

            <table style="border-collapse: collapse; font-size: 13px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="width: 22%;font-size: 14px;font-weight: 400; vertical-align: middle; border:none;">
                        Related Person Type <span style="color: Red;">*</span>
                    </td>

                    <td style="width: 66%; padding: 5px; border:none;">
                        <table style="border:none;">
                            <tr style="border:none;">
                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.relatedPersonType!'')?contains('guardian_minor')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div>
                                    <span style="font-size: 11px;font-weight: 400;">Guardian of Minor</span>
                                </td>
                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.relatedPersonType!'')?contains('assignee')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div>
                                    <span style="font-size: 11px;font-weight: 400;">Assignee</span>
                                </td>
                                <td style="border:none; padding: 2px;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        <#if (data.relatedPersonType!'')?contains('authorized_representative')>
                                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                        </#if>
                                    </div>
                                    <span style="font-size: 11px;font-weight: 400;">Authorised Representative</span>
                                </td>
                            </tr>
                        </table>
                    </td>

                </tr>
            </table>

            <table style="font-weight: normal; border:none;">
                <thead style="border:none;">
                    <tr style="border:none;">
                        <td style="border:none;"></td>
                        <td style="border:none;"><span class="label text-muted">Prefix</span></td>
                        <td style="border:none;"><span class="label text-muted">First Name</span></td>
                        <td style="border:none;"><span class="label text-muted">Middle Name</span></td>
                        <td style="border:none;"><span class="label text-muted">Last Name</span></td>
                    </tr>
                </thead>
                <tbody style="border:none;">
                    <tr style="border:none;">
                        <td style="border:none;">Name *</td>
                            <td style="border:none;"><div class="underline"><#if (data.relatedPrefix!'')?has_content>${data.relatedPrefix!''}<#else>______________</#if></div></td>
                            <td style="border:none;"><div class="underline"><#if (data.relatedFirstName!'')?has_content>${data.relatedFirstName!''}<#else>______________</#if></div></td>
                            <td style="border:none;"><div class="underline"><#if (data.relatedMiddleName!'')?has_content>${data.relatedMiddleName!''}<#else>______________</#if></div></td>
                            <td style="border:none;"><div class="underline"><#if (data.relatedLastName!'')?has_content>${data.relatedLastName!''}<#else>______________</#if></div></td>
                    </tr>
                    <tr style="border:none;">
                        <td style="border:none;"></td>
                        <td style="border:none;"  colspan="5">
                            <span style="font-size: 11px;font-weight: 400;">If KYC number and name is provided, below details of section 6 are optional)</span>
                        </td>
                    </tr>
                </tbody>
            </table>
        <!-- old page break -->
            <label style="font-weight: bold; display: block; margin-top: 5px; margin-bottom: 5px; font-size: 11px; background-color: #eeeeee; padding:5px !important;">
                <span style="font-size: 11px;font-weight: 400;">PROOF OF IDENTITY [POI] OF RELATED PERSON*</span> <span style="font-size: 10px;font-weight: 400;">(please refer instructions (H) at the end)</span>
            </label>

            <table style="border-collapse: collapse; margin-top: 5px; font-size: 13px; width: 100%; border:none;">
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                                <#if (data.passportNumberValue!'')?has_content>
                                    <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                                </#if>
                            </div>
                        <span style="font-size: 11px;font-weight: 400;">A. Passport Number : - </span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                            ${data.passportNumberValue!' '}
                        </span>
                    </td>

                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">Passport Expiry Date : -</span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">

                        </span>
                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                        <#if (data.voterIdCardValue!'')?has_content>
                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                        </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">B. Voter ID Card : - </span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                            ${data.voterIdCardValue!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; border:none;">

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="padding: 5px; width: 50%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                        <#if (data.panCardValue!'')?has_content>
                            <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                        </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">C. PAN Card : - </span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                            ${data.panCardValue!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; width: 30%; border:none;">

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <#if (data.drivingLicenceValue!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">D. Driving Licence : -</span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                            ${data.drivingLicenceValue!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">Driving Licence Expiry Date : - </span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">

                        </span>
                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <#if (data.uidAadhaarValue!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">E. UID (Aadhaar) : - </span>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                            ${data.uidAadhaarValue!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; width: 30%;border:none;">

                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="font-size: 14px; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <#if (data.othersValue!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">Z. Others</span> <span style="font-size: 10px; font-weight: 400;">(any document notified by the central government) : - </span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                            ${data.othersValue!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">Identification Number : - </span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 45%; border-bottom: 1px solid #000000;">
                            ${data.othersIdentificationNumber!' '}
                        </span>
                    </td>
                </tr>
                <tr style="border:none;">
                    <td style="border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                            <#if (data.simplifiedMeasuresDocumentType!'')?has_content>
                                <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                            </#if>
                        </div>
                        <span style="font-size: 11px;font-weight: 400;">S. Simplified Measures Account - Document Type code : - </span>
                         <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                            ${data.simplifiedMeasuresDocumentType!' '}
                        </span>
                    </td>
                    <td style="padding: 5px; width: 30%; border:none;">
                        <span style="font-size: 11px;font-weight: 400;">Identification Number : - </span>
                          <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 45%; border-bottom: 1px solid #000000;">
                            ${data.simplifiedMeasuresIdentificationNumber!' '}
                        </span>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <!-- Seventh Page with Content -->

    <div style="margin-top: 0;">
        <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px !important;">
            <label style="font-weight: bold; display: block; margin-bottom: 0; font-size: 14px; background-color: #A9A9A9; padding:2px !important;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    <#if (data.remarks!'')?has_content>
                        <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                    </#if>
                </div>
                <span style="font-size: 11px;">7. REMARKS (if any)</span>
            </label>

        </div>

        <#list 0..<2 as i>
            <div class="col-sm-12 p-0" style="font-size: 14px; height:20px;">
                <!-- Displaying remark text above the line -->
                <span style="font-size: 14px; font-weight: normal;"><#if i == 0>${data.remarks!''}</#if></span>
                <br />
                <span style="width: 100%;">________________________________________________________________________________________</span>
            </div>
        </#list>
    </div>
<div class="eight-application-declartion-sec">
        <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px !important;">
            <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px !important;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                        <img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" />
                </div>
                <span style="font-size: 11px;">8. APPLICATION DECLARATION</span>
            </label>

        </div>

        <table style="border-collapse: collapse; font-size: 11px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="width: 60%; border:none;">
                    <!-- Order list -->
                    <ul style="font-size: 11px; text-align: justify;padding-left: 10px;">
                        <li style="margin-bottom: 10px;">
                            I hereby declare that the details furnished above are true and correct to the best of my knowledge
                            and belief and I undertake to inform you of any changes therein, immediately. In case any of the
                            above information is found to be false or untrue or misleading or misrepresenting, I am aware
                            that I may be held liable for it.
                        </li>
                        <li>
                            I hereby consent to receiving information from Central KYC Registry through SMS/Email on the
                            above registered number/email address.
                        </li>
                    </ul>

                    <span style="font-size: 11px;">
                        <strong>Date :</strong>
                       <strong><u> ${data.currentDate!''} </u></strong>
                    </span>

                    <span style="font-size: 11px;">
                        <strong>Place :</strong>
                            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                            ${(data.permCity!'')?upper_case}                        </span>
                    </span>
                </td>
                <td style="width: 40%;background: #D1D3D4; text-align: center; vertical-align: bottom; border:none;">
                    <span style="font-size: 10px;font-weight: 400; text-align:center;background: #A9A9A9;display: block; padding: 5px 0;">
                            Signature / Thumb Impression of Applicant
                    </span>
                </td>
            </tr>
        </table>
</div>


<div class="nine-page-attestation-sec" style="margin-top: 20px; ">
        <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px !important;">
            <label style="font-weight: bold; display: block; font-size: 14px; background-color: #A9A9A9; padding:5px !important;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">9. ATTESTATION / FOR OFFICE USE ONLY</span>
            </label>
        </div>

        <table style="font-weight: normal; font-size:14px !important;background: #D1D3D4; border:none;">
            <tbody style="border:none;">
                <tr style="border:none;">
                    <td style="width: 25%; vertical-align: middle; border:none;">
                        <b style="font-size: 11px;">Documents Received </b>
                    </td>
                    <td style="width: 75%;padding: 2px; border:none;">
                        <table style="border:none;">
                            <tr style="border:none;">
                                <td style="border:none; padding: 2px; border:none;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap; border:none;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        </div> <span style="font-size: 11px; font-weight: 400;">Certified Copies</span>
                                    </label>
                                </td>
                                <td style="border:none; padding: 2px; ">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        </div> <span style="font-size: 11px; font-weight: 400;">Client Interviewed by</span>
                                    </label>
                                </td>
                                <td style="border:none; padding: 2px;">
                                    <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                        </div> <span style="font-size: 11px; font-weight: 400;">In - Person verification done by</span>
                                    </label>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>

        <table style="border-collapse: collapse;font-size: 11px; width: 100%;background: #D1D3D4; border:none;">
            <tr style="border:none;">
                <td style="width: 50%; border:none;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px !important;text-align: center;">
                        <span style="font-size: 11px; font-weight: 400;">KYC VERIFICATION CARRIED OUT BY</span>
                    </label>

                    <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                        Date  ______________________________________

                    </span><br />
                    <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                        Emp. Name  ______________________________________

                    </span><br />
                    <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                        Emp. Code  ______________________________________

                    </span><br />
                    <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                        Emp. Designation  ______________________________________

                    </span><br />
                    <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                        Emp. Branch  ______________________________________

                    </span><br />
                    <div class="square-box" style="width: 100%; height: 50px; display: inline-block; text-align: center; line-height: 12px;background: #fff;margin-top: 5px;">
                        <small class="text-muted">
                            [Employee Signature]
                        </small>
                    </div>
                </td>

                <td style="width: 50%; border:none;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px !important;text-align: center;">
                        <span style="font-size: 11px; font-weight: 400;">INSTITUTION DETAILS</span>
                    </label>

                    <span style="font-size: 11px; font-weight: bold; padding: 5px;">
                        <span style="font-size: 11px; font-weight: 400;">Name</span>
                         ______________________________________

                    </span><br />

                    <span style="font-size: 11px; font-weight: bold; padding: 5px;">
                        <span style="font-size: 11px; font-weight: 400;">Code</span>
                         ______________________________________

                    </span><br />

                    <div class="square-box" style="width: 100%; height: 100px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;background: #fff;margin-top: 5px;">
                        <small class="text-muted">[Institution Stamp]</small>
                    </div>
                </td>
            </tr>
        </table>
    </div>

<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 03 -------- </span>




<!--======================Second Holder====================================-->
    <span style="position:absolute; bottom:0; left:40%;display: none;"> -------- 06 -------- </span>
    <div class="page-break"></div>

<#-- KYC Form Part 2 - Converted from Laravel Blade (lines 3285-5789) to FreeMarker -->
<#-- For Flying Saucer XHTML PDF renderer -->
<#-- All @php blocks removed - logic moved to Java service layer -->

<#-- ============================================================ -->
<#-- Sixth Page with Content - Second Holder sections (display:none / N/A) -->
<#-- ============================================================ -->

<div class="container-fluid" style="display: none; border:none;">
    <div class="na-watermark-diagonal" style="font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">
        <label style="font-weight: 400; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px; border:none;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
            </div>

            <span style="font-size: 11px; font-weight: 400; border:none;">4.2 CORRESPONDENCE / LOCAL ADDRESS DETAILS</span>
            <span style="font-size: 10px; font-weight: 400; border:none;">(Please see instruction E at the end)</span>
        </label>


        <table style="border-collapse: collapse; margin-bottom: 20px; font-size: 11px; border:none;">
            <tr style="border:none;">
                <td style="padding: 5px; width: 50%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px; border:none;">Same as Current / Permanent / Overseas Address Details</span>
                    <span style="font-size: 10px; border:none;">(In case of multiple correspondence/local addresses please <b>fill 'Annexure A1'</b>)</span>
                </td>
            </tr>
        </table>
        <div class="col-sm-12 p-2" style="font-size: 11px; height:30px; border:none;">
            <span style="font-size: 11px; font-weight: 400;">Line 1* :</span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 90%; border-bottom: 1px solid #000000;">
            </span>
        </div>

        <div class="col-sm-12 p-2" style="font-size: 11px; height:30px; border:none;">
            <span style="font-size: 11px; font-weight: 400;">Line 2 :</span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 90%; border-bottom: 1px solid #000000;">
            </span>
        </div>

        <div class="row col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px; border:none;">
            <span style="font-size: 11px; font-weight: 400;">Line 3 :</span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 50%; border-bottom: 1px solid #000000;">
            </span>
            <span style="font-size: 11px; font-weight: 400;">City / Town / Village :</span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
            </span>
        </div>

        <div class="row col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px; border:none;">
            <span style="font-size: 11px; font-weight: 400;">District * </span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 18%; border-bottom: 1px solid #000000;">
            </span>&nbsp;

            <span style="font-size: 11px; font-weight: 400;">Pin / Post Code * </span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 18%; border-bottom: 1px solid #000000;">
            </span>&nbsp;

            <span style="font-size: 11px; font-weight: 400;">State / U.T Code * </span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width:18%; border-bottom: 1px solid #000000;">
            </span>&nbsp;
        </div>
         <div class="row col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px; border:none;">

            <span style="font-size: 11px; font-weight: 400;">ISO 3166 Country Code * </span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width:20%; border-bottom: 1px solid #000000;">
            </span>

        </div>

    </div>


    <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">
        <label style="font-weight: 400; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:15px;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
            </div>
            <span style="font-size: 11px; font-weight: 400;">4.3 ADDRESS IN THE JURISDICTION DETAILS WHERE APPLICANT IS RESIDENT OUTSIDE INDIA FOR TAX PURPOSES *</span>
            <span style="font-size: 10px; font-weight: 400;">(Applicable if section 2 is ticked.)</span>
        </label>


        <table style="border-collapse: collapse; margin-bottom: 20px; font-size: 11px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="padding: 5px; width: 50%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px; border:none;">Same as Current / Permanent / Overseas Address Details</span>
                </td>
                <td style="padding: 5px; width: 50%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px; border:none;">Same as Correspondence / Local Address Details</span>
                </td>
            </tr>

        </table>

        <div class="col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px;">
            <span style="font-size: 11px; font-weight: 400;">Line 1* : </span>
            <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 90%; border-bottom: 1px solid #000000;">
            </span>
        </div>
        <div class="col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px;">
            <span style="font-size: 11px; font-weight: 400;">Line 2 : </span>
             <span style="font-size: 11px; font-weight: 400; display: inline-block; width:90%; border-bottom: 1px solid #000000;">
            </span>
        </div>
        <div class="row col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px;">
            <span style="font-size: 11px; font-weight: 400;">Line 3 : </span>
             <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 50%; border-bottom: 1px solid #000000;">
            </span>

            <span style="font-size: 11px; font-weight: 400;">City / Town / Village : </span>
             <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 14%; border-bottom: 1px solid #000000;">
            </span>

        </div>
        <div class="row col-sm-12 p-2 m-0 5" style="font-size: 11px; height:30px;">
            <span style="font-size: 11px; font-weight: 400;">State * </span>
             <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
            </span> &nbsp; &nbsp; &nbsp;
            <span style="font-size: 11px; font-weight: 400;">Zip / Post Code * </span>
             <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 12%; border-bottom: 1px solid #000000;">
            </span>&nbsp;
            <span style="font-size: 11px; font-weight: 400;">ISO 3166 Country Code * </span>
             <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 8%; border-bottom: 1px solid #000000;">
            </span>
        </div>
    </div>


    <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">
       <label style="border:none; font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 10px; text-transform: uppercase;">
            </div>
            <span style="font-size: 11px; border:none;">5 . CONTACT DETAILS</span>
            <span style="font-size: 10px; border:none; font-weight: 400;">
                (All Communications will be sent on provided Mobile No. / Email-Id) (Please refer instructions F at the end)
            </span>
        </label>


        <table style="border-collapse: collapse; margin-top: 10px; font-size: 13px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="padding: 5px; width: 40%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">Tel. (Off)</span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 35%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 40%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">Tel. (Res)</span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 35%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 40%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">FAX</span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 35%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
            </tr>
        </table>
        <table style="border-collapse: collapse; font-size: 13px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="width: 40%; vertical-align: middle; border:none;">
                    Mobile <span style="color: Red;">*</span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 40%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="width: 60%; padding: 5px; border:none;">
                    <table style="border:none;">
                        <tr style="border:none;">
                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                </div> <span style="font-size: 11px;">Self</span>
                            </td>

                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                </div> <span style="font-size: 11px;">Spouse</span>
                            </td>

                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                </div> <span style="font-size: 11px;">Dependent Parent</span>
                            </td>

                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center;">
                                </div> <span style="font-size: 11px;">Dependent Children</span>
                            </td>
                        </tr>
                    </table>

                </td>
            </tr>
        </table>
        <table style="border-collapse: collapse; font-size: 13px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="width: 40%; vertical-align: middle; border:none;">
                    Email Id <span style="color: Red;">*</span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 60%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="width: 60%; padding: 5px; border:none;">
                    <table style="border:none;">
                        <tr style="border:none;">
                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div> <span style="font-size: 11px; font-weight: 400;">Self</span>
                            </td>

                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div> <span style="font-size: 11px; font-weight: 400;">Spouse</span>
                            </td>

                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div> <span style="font-size: 11px; font-weight: 400;">Dependent Parent</span>
                            </td>

                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div> <span style="font-size: 11px; font-weight: 400;">Dependent Children</span>
                            </td>
                        </tr>
                    </table>

                </td>
            </tr>
        </table>
    </div>

    <br />

    <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px; border:none;">
        <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
            </div>
            <span style="font-size: 11px;">6. DETAILS OF RELATED PERSON</span>
            <span style="font-size: 10px;font-weight: 400;">(In case of additional related persons, please <b>fill 'Annexure A1'</b>) (please refer instructions G at the end)</span>
        </label>


        <table style="border-collapse: collapse; margin-top: 3px; background-color: #eeeeee; font-size: 11px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="padding: 5px; width: 25%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">Addition of Related Person</span>
                </td>

                <td style="padding: 5px; width: 75%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">Deletion of Related Person</span>
                    <span style="font-size: 11px;font-weight: 400;padding-left: 20px; padding-right: 5px;">KYC Number of Related Person (if available*)</span>

                       __________________

                     </td>
            </tr>
        </table>

        <table style="border-collapse: collapse; font-size: 13px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="width: 22%;font-size: 14px;font-weight: 400; vertical-align: middle; border:none;">
                    Related Person Type <span style="color: Red;">*</span>
                </td>

                <td style="width: 66%; padding: 5px; border:none;">
                    <table style="border:none;">
                        <tr style="border:none;">
                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div>
                                <span style="font-size: 11px;font-weight: 400;">Guardian of Minor</span>
                            </td>
                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div>
                                <span style="font-size: 11px;font-weight: 400;">Assignee</span>
                            </td>
                            <td style="border:none; padding: 2px;">
                                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                </div>
                                <span style="font-size: 11px;font-weight: 400;">Authorised Representative</span>
                            </td>
                        </tr>
                    </table>
                </td>

            </tr>
        </table>

        <table style="font-weight: normal; border:none;">
            <thead style="border:none;">
                <tr style="border:none;">
                    <td style="border:none;"></td>
                    <td style="border:none;"><span class="label text-muted">Prefix</span></td>
                    <td style="border:none;"><span class="label text-muted">First Name</span></td>
                    <td style="border:none;"><span class="label text-muted">Middle Name</span></td>
                    <td style="border:none;"><span class="label text-muted">Last Name</span></td>
                </tr>
            </thead>
            <tbody style="border:none;">
                <tr style="border:none;">
                    <td style="border:none;">Name *</td>
                        <td style="border:none;"><div class="underline">___________</div></td>
                        <td style="border:none;"><div class="underline">___________</div></td>
                        <td style="border:none;"><div class="underline">___________</div></td>
                        <td style="border:none;"><div class="underline">___________</div></td>
                </tr>
                <tr style="border:none;">
                    <td style="border:none;"></td>
                    <td style="border:none;" colspan="5">
                        <span style="font-size: 11px;font-weight: 400;">If KYC number and name is provided, below details of section 6 are optional)</span>
                    </td>
                </tr>
            </tbody>
        </table>

        <span style="position:absolute; bottom:0; left:40%;"> -------- 07 -------- </span>
<div class="page-break"></div>

<#-- ============================================================ -->
<#-- Second Holder POI with N/A watermark -->
<#-- ============================================================ -->

    <div class="na-watermark-diagonal">

        <label style="font-weight: bold; display: block; margin-top: 5px; margin-bottom: 5px; font-size: 11px; background-color: #eeeeee; padding:5px;">
            <span style="font-size: 11px;font-weight: 400;">PROOF OF IDENTITY [POI] OF RELATED PERSON*</span> <span style="font-size: 10px;font-weight: 400;">(please refer instructions (H) at the end)</span>
        </label>

        <table style="border-collapse: collapse; margin-top: 5px; font-size: 13px; width: 100%; border:none;">
            <tr style="border:none;">
                <td style="padding: 5px; width: 50%; border:none;">
                        <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                        </div>
                    <span style="font-size: 11px;font-weight: 400;">A. Passport Number : - </span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                    </span>
                </td>

                <td style="padding: 5px; width: 30%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">Passport Expiry Date : -</span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
            </tr>
            <tr style="border:none;">
                <td style="padding: 5px; width: 50%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">B. Voter ID Card : - </span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; border:none;">
                </td>
            </tr>
            <tr style="border:none;">
                <td style="padding: 5px; width: 50%; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">C. PAN Card : - </span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 30%; border:none;">
                </td>
            </tr>
            <tr style="border:none;">
                <td style="border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">D. Driving Licence : -</span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 30%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">Driving Licence Expiry Date : -</span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
            </tr>
            <tr style="border:none;">
                <td style="border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">E. UID (Aadhaar) : - </span>
                    <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 30%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 30%;border:none;">
                </td>
            </tr>
            <tr style="border:none;">
                <td style="font-size: 14px; border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">Z. Others</span> <span style="font-size: 10px; font-weight: 400;">(any document notified by the central government) : - </span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 30%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">Identification Number : - </span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 45%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
            </tr>
            <tr style="border:none;">
                <td style="border:none;">
                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
                    </div>
                    <span style="font-size: 11px;font-weight: 400;">S. Simplified Measures Account - Document Type code : - </span>
                     <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 15%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
                <td style="padding: 5px; width: 30%; border:none;">
                    <span style="font-size: 11px;font-weight: 400;">Identification Number : - </span>
                      <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 45%; border-bottom: 1px solid #000000;">
                    </span>
                </td>
            </tr>
        </table>
    </div>

    </div>
</div>

<#-- ============================================================ -->
<#-- Seventh Page with Content (display:none - Second Holder Remarks) -->
<#-- ============================================================ -->

<div class="container-fluid" style="margin-top: 20px; display: none;">
    <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px;">
        <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;">
            </div>
            <span style="font-size: 11px;">7. REMARKS (if any)</span>
        </label>

    </div>

    <#list 0..<2 as i>
        <div class="col-sm-12 p-2" style="font-size: 14px; height:30px;">
            <span style="font-size: 14px; font-weight: normal;"></span>
            <br />
            <span style="width: 100%;"></span>
        </div>
    </#list>
</div>

<#-- ============================================================ -->
<#-- Eighth Page with Content (display:none - Second Holder Application Declaration) -->
<#-- ============================================================ -->

<div class="container-fluid" style="margin-top: 20px; display: none;">
    <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px;">
        <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 14px; background-color: #A9A9A9; padding:5px;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
            <span style="font-size: 11px;">8. APPLICATION DECLARATION</span>
        </label>

    </div>

    <table style="border-collapse: collapse; font-size: 11px; width: 100%; border:none;">
        <tr style="border:none;">
            <td style="width: 60%; border:none;">
                <ul style="font-size: 11px; text-align: justify;padding-left: 10px;">
                    <li style="margin-bottom: 10px;">
                        I hereby declare that the details furnished above are true and correct to the best of my knowledge
                        and belief and I undertake to inform you of any changes therein, immediately. In case any of the
                        above information is found to be false or untrue or misleading or misrepresenting, I am aware
                        that I may be held liable for it.
                    </li>
                    <li>
                        I hereby consent to receiving information from Central KYC Registry through SMS/Email on the
                        above registered number/email address.
                    </li>
                </ul>

                <span style="font-size: 11px;">
                    <strong>Date :</strong>
                   ______________
                </span>

                <span style="font-size: 11px;">
                    <strong>Place :</strong>
                        <span style="font-size: 11px; font-weight: 400; display: inline-block; width: 20%; border-bottom: 1px solid #000000;">
                    </span>
                </span>
            </td>
            <td style="width: 40%;background: #D1D3D4; text-align: center; vertical-align: bottom; border:none;">
                <span style="font-size: 10px;font-weight: 400; text-align:center;background: #A9A9A9;display: block; padding: 5px 0;">
                        Signature / Thumb Impression of Applicant
                </span>
            </td>
        </tr>
    </table>
</div>

<#-- ============================================================ -->
<#-- Ninth Page with Content (display:none - Second Holder Attestation / Office Use) -->
<#-- ============================================================ -->

<div class="container-fluid" style="margin-top: 20px; display: none;">
    <div style="font-family: Arial, sans-serif; width: fit-content; font-size:11px;">
        <label style="font-weight: bold; display: block; font-size: 14px; background-color: #A9A9A9; padding:5px;">
            <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
            <span style="font-size: 11px;">9. ATTESTATION / FOR OFFICE USE ONLY</span>
        </label>
    </div>

    <table style="font-weight: normal; font-size:14px;background: #D1D3D4; border:none;">
        <tbody style="border:none;">
            <tr style="border:none;">
                <td style="width: 25%; vertical-align: middle; border:none;">
                    <b style="font-size: 11px;">Documents Received </b>
                </td>
                <td style="width: 75%;padding: 5px; border:none;">
                    <table style="border:none;">
                        <tr style="border:none;">
                            <td style="border:none; padding: 2px; border:none;">
                                <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap; border:none;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                    </div> <span style="font-size: 11px; font-weight: 400;">Certified Copies</span>
                                </label>
                            </td>
                            <td style="border:none; padding: 2px;">
                                <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                    </div> <span style="font-size: 11px; font-weight: 400;">Client Interviewed by</span>
                                </label>
                            </td>
                            <td style="border:none; padding: 2px;">
                                <label style="display: flex; align-items: center; gap: 5px; white-space: nowrap;">
                                    <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;">
                                    </div> <span style="font-size: 11px; font-weight: 400;">In - Person verification done by</span>
                                </label>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </tbody>
    </table>

    <table style="border-collapse: collapse;font-size: 11px; width: 100%;background: #D1D3D4; border:none;">
        <tr style="border:none;">
            <td style="width: 50%; border:none;">
                <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px;text-align: center;">
                    <span style="font-size: 11px; font-weight: 400;">KYC VERIFICATION CARRIED OUT BY</span>
                </label><br />

                <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                    Date  ______________________________________

                </span><br /><br />
                <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                    Emp. Name  ______________________________________

                </span><br /><br />
                <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                    Emp. Code  ______________________________________

                </span><br /><br />
                <span style="font-size: 11px; font-weight: 400; padding: 5px;">
                    Emp. Designation  ______________________________________

                </span><br /><br />
                <span style="font-size: 11px; font-weight: 40; padding: 5px;">
                    Emp. Branch  ______________________________________

                </span><br /><br />
                <div class="square-box" style="width: 100%; height: 50px; display: inline-block; text-align: center; line-height: 12px;background: #fff;">
                    <small class="text-muted">
                        [Employee Signature]
                    </small>
                </div>
            </td>
            <td style="width: 50%; border:none;">
                <label style="font-weight: bold; display: block; margin-bottom: 5px; font-size: 11px; background-color: #A9A9A9; padding:5px;text-align: center;">
                    <span style="font-size: 11px; font-weight: 400;">INSTITUTION DETAILS</span>
                </label><br />

                <span style="font-size: 11px; font-weight: bold; padding: 5px;">
                    <span style="font-size: 11px; font-weight: 400;">Name</span>
                     ______________________________________

                </span><br /><br />

                <span style="font-size: 11px; font-weight: bold; padding: 5px;">
                    <span style="font-size: 11px; font-weight: 400;">Code</span>
                    ______________________________________

                </span><br /><br />

                <div class="square-box" style="width: 100%; height: 123px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px;background: #fff;">
                    <small class="text-muted">[Institution Stamp]</small>
                </div>
            </td>
        </tr>
    </table>
</div>


<#-- ============================================================ -->
<#-- INSTRUCTIONS / CHECKLIST / GUIDELINES -->
<#-- ============================================================ -->

<div class="WordSection3">
    <div class="row">
        <p class="instructions-header" style="font-size: 11px; margin:20px 20px 0;">
            CENTRAL KYC REGISTRY | Instructions / Check list / Guidelines for filling Individual KYC Application Form
        </p>
        <p class="list-paragraph" style="font-size: 11px; margin:0 20px;">General Instructions:</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li>Fields marked with '*' are mandatory fields.</li>
            <li>Tick '<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />' wherever applicable.</li>
            <li>Self-Certification of documents is mandatory.</li>
            <li>Please fill the form in English and in BLOCK Letters.</li>
            <li>Please fill all dates in DD-MM-YYYY format.</li>
            <li>Wherever state code and country code is to be furnished, the same should be the two-digit code as per Indian Motor Vehicle, 1988 and ISO 3166 country code respectively list of which is available at the end.</li>
            <li>KYC number of applicant is mandatory for updation of KYC details.</li>
            <li>For particular section update, please tick () in the box available before the section number and strike off the sections not required to be updated.</li>
            <li>In case of 'Small Account type' only personal details at section number 1 and 2, photograph, signature and self-certification required.</li>
        </ol>
        <p class="list-paragraph" style="font-size:11px; margin:0 20px;">A. Clarification / Guidelines on filling 'Personal Details' section</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li><b>Name:</b> Please state the name with Prefix (Mr/Mrs/Ms/Dr/etc.). The name should match the name as mentioned
               in the Proof of Identity submitted failing which the application is liable to be rejected.</li>
               <li>Either <b>mother's/father's name or spouse's</b> name is to be mandatorily furnished. In case PAN is not available father's name is mandatory.</li>
        </ol>
        <p class="list-paragraph" style="font-size:11px; margin:0 20px;">B. Clarification / Guidelines on filling details if applicant residence for tax purposes in jurisdiction(s) outside India</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li><b>Tax identification Number (TIN):</b> TIN need not be reported if it has not been issued by the jurisdiction. However, if the said jurisdiction has issued a high integrity number with an equivalent level of identification (a "Functional equivalent"), the same may be reported. Examples of that type of number for individual include, a social security/insurance number, citizen/personal identification/services code/number, and resident registration number)</li>
        </ol>
        <p class="list-paragraph" style="font-size:11px; margin:0 20px;">C. Clarification / Guidelines on filling 'Proof of Identity [Pol]' section</p>
        <ol class="list-font" style="margin:0 0 5px;padding-left: 20px;">
            <li>If driving license number or passport is provided as proof of identity then expiry date is to be mandatorily furnished.</li>
            <li>Mention identification / reference number if 'Z- Others (any document notified by the central government)' is ticked.</li>
            <li>In case of Simplified Measures Accounts for verifying the identity of the applicant, any one of the following documents can also be submitted and undernoted relevant code may be mentioned in point 3 (S).
                <br />
                <table class="table table-bordered" style="border:none;margin-top: 10px;margin-bottom: 0;">
                    <tr style="background: #D1D3D4; border:none;">
                        <td style="font-size: 11px;font-weight: bold;width: 12%; border:none;">Document Code</td>
                        <td style="font-size: 11px;font-weight: bold;width: 88%; border:none;">Description</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">01</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Identity card with applicant's photograph issued by Central/ State Government Departments, Statutory/ Regulatory Authorities, Public Sector Undertakings, Scheduled Commercial Banks, and Public Financial Institutions.</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px; border:none;">02</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Letter issued by a gazetted officer, with a duly attested photograph of the person.</td>
                    </tr>
                </table>
            </li>
        </ol>
        <ol class="list-font" style="margin:0 0 5px; padding-left: 15px;">
            <li>To be filled only in case the PoA is not the local address or address where the customer is currently residing. No separate PoA is required to be submitted.</li>
        </ol><p class="list-paragraph" style="font-size:11px; margin:0px 20px;">D. Clarification / Guidelines on filling 'Proof of Address [PoA] - Current / Permanent / Overseas Address details' section</p>
        <ol class="list-font" style="margin:0 0 5px;padding-left: 20px;">
            <li>PoA to be submitted only if the submitted Pol does not have an address or address as per Pol is invalid or not in force.</li>
            <li>State / U.T Code and Pin / Post Code will not be mandatory for Overseas addresses.</li>
            <li>in case of Simplified Measures Accounts for verifying the address of the applicant, any one of the following documents can also be submitted and undernoted relevant code may be mentioned in point 4.1.
                <br />

    <table class="table table-bordered" style="border:none;margin-top: 10px;margin-bottom: 0;">
                    <tr style="background: #D1D3D4; border:none;">
                        <td style="font-size: 11px;font-weight: bold;width: 12%; border:none;">Document Code</td>
                        <td style="font-size: 11px;font-weight: bold;width: 88%; border:none;">Description</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">01</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Utility bill which is not more than two months old of any service provider (electricity, telephone, post-paid mobile phone, piped gas, water bill).</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">02</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Property or Municipal Tax receipt.</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">03</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Bank account or Post Office savings bank account statement.</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">04</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Pension or family pension payment orders (PPOs) issued to retired employees by Government Departments or Public Sector Undertakings, if they contain the address.</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">05</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Letter of allotment of accommodation from employer issued by State or Central Government departments, statutory or regulatory bodies, public sector undertakings, scheduled commercial banks, financial institutions and listed companies. Similarly, leave and license agreements with such employers allotting official accommodation.</td>
                    </tr>
                    <tr style="border:none;">
                        <td style="text-align: left;font-size: 11px;vertical-align: top; border:none;">06</td>
                        <td style="text-align: left;font-size: 11px; border:none;">Documents issued by Government departments of foreign jurisdictions and letter issued by Foreign Embassy or Mission in India.</td>
                    </tr>
                </table>
            </li>
        </ol>
        <p class="list-paragraph" style="font-size:11px; margin:0 20px;">E. Clarification / Guidelines on filling 'Proof of Address [PoA] - Correspondence / Local Address details' section</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li>To be filled only in case the PoA is not the local address or address where the customer is currently residing. No separate PoA is required to be submitted.</li>
            <li>In case of multiple correspondence / local addresses. Please fill <b>'Annexure A1'</b></li>
        </ol>
        <p class="list-paragraph" style="font-size:11px; margin:0 20px;">F. Clarification / Guidelines on filling 'Contact details' section</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li>Please mention two-digit country code and 10 digit mobile number (e.g. for Indian mobile number mention 91-9999999999).</li>
            <li>Do not add '0' in the beginning of Mobile number.</li>
        </ol>
        <p class="list-paragraph" style="font-size:11px; margin:0 20px;">G. Clarification / Guidelines on filling 'Related Person details' section</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li>Provide KYC number of related person if available.</li>
        </ol>
        <p class="list-paragraph" style="font-size:11px;margin:0 20px;">H. Clarification / Guidelines on filling 'Related Person details - Proof of Identity [Pol] of Related Person' section</p>
        <ol class="list-font" style="margin:0 0 5px;">
            <li>Mention identification / reference number if 'Z- Others (any document notified by the central government)' is ticked.</li>
        </ol>

        <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 04 -------- </span>

        <div class="page-break"></div>
<br />

<#-- ============================================================ -->
<#-- STATE / UT CODES TABLE -->
<#-- ============================================================ -->

        <p class="instructions-header" style="font-size: 11px; text-align: center;margin-bottom:0;">
            List of two digit state / U.T codes as per Indian Motor Vehicle Act, 1988
        </p>
        <table class="table table-bordered p-3" style="border:none;margin-bottom:0;margin-top:0;">
            <tr style="background: #dcddde; margin:0 20px; border:none;">
                <td style="font-size: 11px;font-weight: bold;text-align: left; border:none;">State/U.T.</td>
                <td style="font-size: 11px;font-weight: bold;text-align: center; border:none;">Code</td>
                <td style="font-size: 11px;font-weight: bold;text-align: left; border:none;">State/U.T.</td>
                <td style="font-size: 11px;font-weight: bold;text-align: center; border:none;">Code</td>
                <td style="font-size: 11px;font-weight: bold;text-align: left; border:none;">State/U.T.</td>
                <td style="font-size: 11px;font-weight: bold;text-align: center; border:none;">Code</td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Andaman &amp; Nicobar</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">AN</td>
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Himachal Pradesh</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">HP</td>
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Pondicherry</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">PY</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Andhra Pradesh</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">AP</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Jammu &amp; Kashmir</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">JK</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Punjab</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">PB</td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Arunachal Pradesh</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">AR</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Jharkhand</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">JH</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Rajasthan</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">RJ</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Assam</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">AS</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Karnataka</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">KA</td>
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Sikkim</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">SK</td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Bihar</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">BR</td>
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Kerala</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">KI</td>
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Tamil Nadu</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">TN</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Chandigarh</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">CH</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Lakshadweep</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">LO</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Telangana</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">TS</td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Chattisgarh</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">CG</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Madhya Pradesh</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">MP</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Tripura</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">TR</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Dadra and Nagar Haveli</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">DN</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Maharashtra</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">MH</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Uttar Pradesh</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">UP</td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Daman &amp; Diu</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">DD</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Manipur</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">MN</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Uttarakhand</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">UA</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Delhi</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">DL</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Meghalaya</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">ML</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">West Bengal</td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;">WB</td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Goa</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">GA</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Mizoram</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">MZ</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Other</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">XX</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Gujarat</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">GJ</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Nagaland</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">NL</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;"></td>
                <td style="text-align: left;font-size: 11px;text-align: center; border:none;"></td>
            </tr>
            <tr style="background: #D1D3D4; border:none;">
                <td style="text-align: left;font-size: 11px;text-align: left; border:none;">Haryana</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">HR</td>
                <td style="text-align: left;font-size: 11px;text-align: left;border:none;">Orissa</td>
                <td style="text-align: left;font-size: 11px;text-align: center;border:none;">OR</td>
                <td style="text-align: left;font-size: 11px;text-align: left;background: #ffffff; border:none;"></td>
                <td style="text-align: left;font-size: 11px;text-align: center;background: #ffffff; border:none;"></td>
            </tr>
        </table>

<#-- ============================================================ -->
<#-- ISO 3166 COUNTRY CODES TABLE -->
<#-- ============================================================ -->

        <p class="instructions-header" style="font-size: 11px; text-align: center;margin-bottom: 0;">
            List of ISO 3166 two-digit Country Code
        </p>
        <table class="table table-bordered p-3 main-table-row" style="border:none;">
            <tr style="background: #D1D3D4; border:none;">
                <td style="font-size: 11px;font-weight: bold;width: 10%;text-align: left; border:none;">Country</td>
                <td style="font-size: 11px;font-weight: bold;width: 15%;text-align: center; border:none;">Country Code</td>
                <td style="font-size: 11px;font-weight: bold;width: 10%;text-align: left; border:none;">Country</td>
                <td style="font-size: 11px;font-weight: bold;width: 15%;text-align: center; border:none;">Country Code</td>
                <td style="font-size: 11px;font-weight: bold;width: 10%;text-align: left; border:none;">Country</td>
                <td style="font-size: 11px;font-weight: bold;width: 15%;text-align: center; border:none;">Country Code</td>
                <td style="font-size: 11px;font-weight: bold;width: 10%;text-align: left; border:none;">Country</td>
                <td style="font-size: 11px;font-weight: bold;width: 15%;text-align: center; border:none;">Country Code</td>
            </tr>
            <tr style="background: #dcddde; border:none;">
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Afghanistan</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">AF</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Dominican Republic</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">DO</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Libya</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">LY</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Saint Pierre and Miquelon</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">PM</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Aland Islands</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">AX</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Ecuador</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">EC</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Liechtenstein</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">LI</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Saint Vincent and the Grenadines</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">VC</td>
            </tr>
            <tr style="background: #dcddde; border:none;">
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Albania</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center;border:none;">AL</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Egypt</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">EG</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Lithuania</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">LT</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Samoa</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">WS</td>
            </tr>
            <tr style="border:none;">
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Algeria</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">DZ</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">El Salvador</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">SV</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Luxembourg</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">LU</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">San Marino</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">SM</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">American Samoa</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">AS</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Equatorial Guinea</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">GQ</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Macao</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">MO</td>
                <td style="text-align: left;font-size: 10px;width: 15%; text-align: left; border:none;">Sao Tome and Principe J</td>
                <td style="text-align: left;font-size: 10px;width: 10%; text-align: center; border:none;">ST</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Andorra</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AD</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Eritrea</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ER</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Macedonia, the former Yugoslav Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MK</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Saudi Arabia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SA</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Angola</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Estonia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">EE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Madagascar</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Senegal</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SN</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Anguilla</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Ethiopia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ET</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Malawi</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Serbia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">RS</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Antarctica</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AQ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Falkland Islands (Malvinas)</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">FK</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Malaysia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MY</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Seychelles</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SC</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Antigua and Barbuda</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Faroe Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">FO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Maldives</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MV</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Sierra Leone</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SL</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Argentina</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Fiji</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">FJ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Mali</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ML</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Singapore</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SG</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Armenia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Finland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">FI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Malta</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Sint Maarten (Dutch part)</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SX</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Aruba</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">France</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Marshall Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MH</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Slovakia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">Sk</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Australia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">French Guiana</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Martinique</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MQ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Slovenia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SL</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Austria</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">French Polynesia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Mauritania</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Solomon Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SB</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Azerbaijan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AZ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">French Southern Territories</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Mauritius</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Somalia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">So</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bahamas</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BS</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Gabon</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Mayotte</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">YT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">South Africa</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ZA</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bahrain</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BH</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Gambia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Mexico</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MX</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">South Georgia and the South Sandwich Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GS</td>
            </tr>
        <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 05 -------- </span>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bangladesh</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BD</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Georgia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Micronesia, Federated States of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">FM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">South Sudan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SS</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Barbados</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BB</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Germany</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">DE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Moldova, Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">MD</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Spain</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ES</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Belarus</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BY</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Ghana</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GH</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Monaco</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MC</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Sri Lanka</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">LK</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Belgium</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Gibraltar</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Mongolia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Sudan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SD</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Belize</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BZ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Greece</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Montenegro</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ME</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Suriname</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SR</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Benin</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BJ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Greenland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GL</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Monteserrat</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MS</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Svalbard and Jan Mayen</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SJ</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bermuda</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Grenada</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GD</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Morocco</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Swaziland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SZ</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bhutan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Guadeloupe</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GP</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Mozambique</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MZ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Sweden</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SE</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bolivia, Plurinational State of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Guam</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Myanmar</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Switzerland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CH</td>
            </tr>

        <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 06 -------- </span>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bonaire, Sint Eustatius and Saba</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BQ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Gautemala</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Namibia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Syrian Arab Republic</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">SY</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bosnia and Herzegovina</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Guernsey</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Nauru</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Taiwan, Province of China</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TW</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Botswana</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Guinea</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Nepal</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NP</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Tajikistan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TJ</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bouvet Island</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BV</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Guinea-Bissau</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Netherlands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NL</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Tanzania, United Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TZ</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Brazil</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Guyana</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GY</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">New Caledonia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NC</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Thailand</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TH</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">British Indian Ocean Territory</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Haiti</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">HT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">New Zealand</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NZ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Timor-Leste</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TL</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Brunei Darussalam</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Heard Island and McDonald Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">HM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Nicaragua</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Togo</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TG</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Bulgaria</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Holy See (Vatican City State) </td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">VA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Niger</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Tokelau</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TK</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Burkina Faso</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Honduras</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">HN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Nigeria</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Tonga</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TO</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Burundi</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">BI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Hong Kong</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">HK</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Niue</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Trinidad and Tobago</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TT</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Cabo Verde</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CV</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Hungary</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">HU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Norfolk Island</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Tunisia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TN</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Cambodia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">KH</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Iceland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IS</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Northern Mariana Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MP</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Turkey</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TR</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Cameroon</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">India</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Norway</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">NO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Turkmenistan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TM</td>
            </tr>

            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Canada</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Indonesia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">ID</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Oman</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">OM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Turks and Caicos Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TC</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Cayman Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">KY</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Iran, Islamic Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Pakistan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PK</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Tuvalu</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TV</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Central African Republic</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Iraq</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IQ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Palau</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Uganda</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">UG</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Chad</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">TD</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Ireland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Palestine, State of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PS</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Ukraine</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">UA</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Chile</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CL</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Isle of Man</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Panama</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">United Arab Emirates</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">AE</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">China</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Israel</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IL</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Papua New Guinea</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">United Kingdom</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">GB</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Christmas Island</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CX</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Italy</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">IT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Paraguay</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PY</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">United States</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">US</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Cocos (Keeling) Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CC</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Jamaica</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">JM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Peru</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">United States Minor Outlying Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">UM</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Colombia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Japan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">JP</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Philippines</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PH</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Uruguay</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">UY</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Comoros</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">KM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Jersey</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">JE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Pitcairn</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Uzbekistan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">UZ</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Congo</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Jordan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">JO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Poland</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PL</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Vanuatu</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">VU</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Congo, the Democratic Republic of the</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">CD</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Kazakhstan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">KZ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Portugal</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">PT</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Venezuela, Bolivarian Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">VE</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Cook Islands</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CK</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Kenya</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Puerto Rico</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">PR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Viet Nam</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">VN</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Costa Rica</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Kiribati</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Qatar</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">QA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Virgin Islands, British</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">VG</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Cote d'ivoire ! Cote d'ivoire</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CI</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Korea, Democratic People's Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KP</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Reunion !Reunion</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">RE</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Virgin Islands, U.S.</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">VI</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Croatia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">HR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Korea, Republic of</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Romania</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">RO</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Wallis and Futuna</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">WF</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Cuba</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Kuwait</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Russian Federation</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">RU</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Western Sahara</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">EH</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Curacao !Curacao</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Kyrgyzstan</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KG</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Rwanda</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">RW</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Yemen</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">YE</td>
            </tr>

        <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Cyprus</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CY</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Lao People's Democratic Republic</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">LA</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Saint Barthelemy ISaint Barthelemy</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">BL</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Zambia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">ZM</td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Czech Republic</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">CZ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Lativa</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">LV</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Saint Helena, Ascension and Tristan da Cunha</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">SH</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Zimbabwe</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">ZW</td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Denmark</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">DK</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Lebanon</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">LB</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Saint Kitts and Nevis</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">KN</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;"></td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;"></td>
            </tr>
            <tr>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Djibouti</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center;border:none;">DJ</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left;border:none;">Lesotho</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">LS</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Saint Lucia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">LC</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;"></td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;"></td>
            </tr>
            <tr style="background: #dcddde;">
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Dominica</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">DM</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Liberia</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">LR</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;">Saint Martin (French part)</td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;">MF</td>
                <td style="text-align: left;font-size: 10px; width: 15%; text-align: left; border:none;"></td>
                <td style="text-align: left;font-size: 10px; width: 10%; text-align: center; border:none;"></td>
            </tr>
        </table>
    </div>
</div>
<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 07 -------- </span>

<#-- ============================================================ -->
<#-- PART II - TRADING ACCOUNT RELATED DETAILS -->
<#-- ============================================================ -->

<div class="WordSectionneww100">
    <div style="text-align: right;">
        <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
    </div>
    <div>
        <p style="font-size: 11px;text-align: center;margin-bottom: 5px;">PART II - TRADING ACCOUNT RELATED DETAILS</p>
    </div>

<#-- Section A: OTHER DETAILS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="5" style="font-size: 10px;font-weight: bold;text-align: left;">A. OTHER DETAILS</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="5" style="font-weight: bold;font-size: 11px;text-align: left;">Gross Annual Income Details (please specify): Income Range per annum: (Rs. in lacs)</td>
        </tr>

<tr style="border:1px solid #000;">

<td style="font-size:11px;text-align:center;border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;">
<#if (data.grossIncome!'') == '100000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Below 1</span>
</td>

<td style="font-size:11px;text-align:center;border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;">
<#if (data.grossIncome!'') == '500000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">1 to 5</span>
</td>

<td style="font-size:11px;text-align:center;border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;">
<#if (data.grossIncome!'') == '1000000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">5 to 10</span>
</td>

<td style="font-size:11px;text-align:center;border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;">
<#if (data.grossIncome!'') == '2000000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">10 to 25</span>
</td>

<td style="font-size:11px;text-align:center;border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;">
<#if (data.grossIncome!'') == '2700000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Above 25</span>
</td>

</tr>
    </table>
    <div>
        <p style="font-size: 11px;text-align: center;margin-bottom: 5px;">OR</p>
    </div>
    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="border: 1px solid #000000;">
            <th style="font-size: 11px;font-weight: bold;text-align: left; width: 20%; border: 1px solid #000000;">Net-worth as on date</th>
            <td colspan="5" style="font-size: 11px;text-align: center; border: 1px solid #000000;"><b>Rs. ${data.netWorth!''}</b> <span style="font-style:italic;float: right;">(Net worth should not be older than 1 year)</span></td>
        </tr>

<#-- Occupation rows -->
       <tr style="border:1px solid #000;">
<th rowspan="2" style="font-size:11px;text-align:left;border:1px solid #000;width:20%;">
Occupation <span style="font-weight:normal;">(please tick any one and give brief details):</span>
</th>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Salaried - Pvt Sector'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Private Sector</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Salaried - Public Sector'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Public Sector</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Salaried - GOVT Service'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Government Service</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Business'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Business</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Student'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Student</span>
</td>

</tr>

<tr style="border:1px solid #000;">

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Professional'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Professional</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Agriculturist'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Agriculturist</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Retired'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Retired</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Housewife'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Housewife</span>
</td>

<td style="border:1px solid #000;">
<div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupationType!'') == 'Others'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="font-size:11px;">Others</span>
</td>

</tr>


<tr style="border: 1px solid #000000;">
            <th style="font-size: 11px;font-weight: bold;text-align: left; width: 20%; border: 1px solid #000000;">Nature of Organisation:</th>
            <td colspan="5" style="font-size: 11px;text-align: left; border: 1px solid #000000;">${(data.natureOfOrganisation!'NA')?upper_case}</td>
        </tr>


<#-- PEP Declaration rows -->
  <tr style="border:1px solid #000000;">
    <th rowspan="2" style="font-size:11px;font-weight:bold;text-align:left;border:1px solid #000000;width:20%;">
        Please tick, if applicable:
    </th>

    <td colspan="2" style="border:1px solid #000;">
        <div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
            <#if (data.polExposed!'') == '1'>
                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
            </#if>
        </div>
        <span style="font-size:11px;">Politically Exposed Person (PEP)</span>
    </td>

    <td colspan="3" style="border:1px solid #000;">
        <div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
            <#if (data.polExposedRelated!'') == '1'>
                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
            </#if>
        </div>
        <span style="font-size:11px;">Related to a Politically Exposed Person (PEP)</span>
    </td>
</tr>

<tr style="border:1px solid #000000;">

    <td colspan="2" style="border:1px solid #000;">
        <div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
            <#if (data.polExposed!'') == '0'>
                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
            </#if>
        </div>
        <span style="font-size:11px;">Not a Politically Exposed Person (PEP)</span>
    </td>

    <td colspan="3" style="border:1px solid #000;">
        <div style="width:10px;height:10px;border:1px solid #000;display:inline-block;text-align:center;">
            <#if (data.polExposedRelated!'') == '0'>
                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
            </#if>
        </div>
        <span style="font-size:11px;">Not Related to a Politically Exposed Person (PEP)</span>
    </td>

</tr>
    </table>


<#-- Section B: BANK ACCOUNT(S) DETAILS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="3" style="font-size: 10px;font-weight: bold;text-align: left;">B. BANK ACCOUNT(S) DETAILS <span style="font-size: 11px;font-weight: normal;">Please provide cancelled cheque leaf for MICR &amp; IFSC Code</span></td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; font-weight: bold;border: 1px solid #000000;">
                Bank Name
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                  ${(data.bankName!'')?upper_case}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.pisBankName!'')?upper_case}
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                Branch Address
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.bankBranchAddress!'')?upper_case}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.pisBankBranchAddress!'')?upper_case}
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                City/Town/Pincode
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
               ${(data.bankDetailsCity!'')?upper_case} - ${data.bankDetailsZipCode!''}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.pisBankCity!'')?upper_case}<#if (data.pisBankZipCode!'')?has_content> - ${data.pisBankZipCode!''}</#if>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                Bank Account No.
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.bankAccountNo!'')?upper_case}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.pisBankAccountNumber!'')?upper_case}
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                Account Type
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                OTHERS
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                PIS
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                MICR No.
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${data.bankDetailsMicr!''}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${data.pisBankMicr!''}
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                IFSC Code
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.ifscCode!'')?upper_case}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.pisBankIfscCode!'')?upper_case}
            </td>
        </tr>
    </table>

<#-- Section C: DEPOSITORY ACCOUNT(S) DETAILS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="3" style="font-size: 10px;font-weight: bold;text-align: left;">C. DEPOSITORY ACCOUNT(S) DETAILS, IF AVAILABLE</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; border: 1px solid #000000;">
                Depository Participant Name
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                VENTURA SECURITIES LIMITED
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                Depository Name
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                <strong>NSDL</strong> / CDSL
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                <strong>NSDL</strong> / CDSL
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                Beneficiary Name
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                ${(data.investorFirstName!'')?upper_case} ${(data.investorMiddleName!'')?upper_case} ${(data.investorLastName!'')?upper_case}
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                DP ID
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                IN303116
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">
                IN303116
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;">
                Beneficiary ID (BO ID)
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;border: 1px solid #000000;">

            </td>
        </tr>
    </table>

<#-- Section D: TRADING PREFERENCE -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="7" style="font-size: 10px;font-weight: bold;text-align: left;">D. TRADING PREFERENCE</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="7" style="font-size: 11px;text-align: left;">Please sign in the relevant boxes where you wish to trade. Please strike off the segment not chosen by you.</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; border: 1px solid #000000;font-weight: bold;">
                Exchanges
            </td>
            <td colspan="5" style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">
                NSE &amp; BSE
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;">
                MCX, NCDEX, BSE &amp; NSE
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; border: 1px solid #000000;font-weight: bold;">
                All Segments
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">
                Cash / Mutual Fund
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">
                F&amp;O
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">
                Currency
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">
                Debt
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">
                Commodity Derivatives
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;"></td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;"></td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;"></td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;"></td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;">Not Applicable</td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align:center;">Not Applicable</td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align:center;">Not Applicable</td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align:center;">Not Applicable</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="7" style="font-size: 11px;border: 1px solid #000000;">
                If you do not wish to trade in any of segments / Mutual Fund, please mention here. <br />
                _______________________________________________________________________________________________________
            </td>
        </tr>
    </table>

<#-- Section E: PAST ACTIONS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="2" style="font-size: 10px;font-weight: bold;text-align: left;">E. PAST ACTIONS</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;text-align: left; width: 50%;">
                Details of any action/proceedings initiated/pending/ taken by SEBI/ Stock exchange/any other authority against the applicant/constituent or its Partners/promoters/whole time directors/authorized persons in charge of dealing in securities during the last 3 years:
            </td>
            <td style="font-size: 11px; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; border: 1px solid #000000; font-weight: bold; background: #d1d3d4; width: 50%;">
                Please provide your GST No
            </td>
            <td style="font-size: 11px;border: 1px solid #000000;font-weight: bold;text-align: center;">

            </td>
        </tr>
    </table>

    <div style="width: 30%; float:right;">
        <p style="font-size: 11px;text-align: center;">
            <span>_______________________________</span><br />
            Signature of the Applicant
        </p>
    </div>

    <br />

    <br />

<#-- Section F: INVESTMENT / TRADING EXPERIENCE -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="3" style="font-size: 10px;font-weight: bold;text-align: left;">F. INVESTMENT / TRADING EXPERIENCE</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; border: 1px solid #000000;font-weight: bold;">
                <div class="square-box" style="width: 10px; height: 10px; border: 1px solid #000; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">No Prior Experience </span>
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;font-weight: bold;">
                <u><strong>Not Applicable</strong></u><span style="font-size: 11px;">Years in Commodities</span>
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;font-weight: bold;">
                <u><strong>${(data.investmentExperienceYears!'')?upper_case}</strong></u><span style="font-size: 11px;">Years in other investment related fields</span>
            </td>
        </tr>
    </table>
<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 08 -------- </span>
      <div class="page-break"></div>
  <br />

<#-- Section G: DEALINGS THROUGH SUB-BROKERS AND OTHER STOCK BROKERS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="4" style="font-size: 10px;font-weight: bold;text-align: left;">G. DEALINGS THROUGH SUB-BROKERS AND OTHER STOCK BROKERS</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="4" style="font-size: 11px;font-weight: bold;text-align: left;">If client is dealing through the sub-broker/authorised person and other stock brokers, provide the following details:</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; border: 1px solid #000000;">
                Sub-broker's Name:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td rowspan="5" style="font-size: 11px; text-align: left; border: 1px solid #000000;">
                Registered office address:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">
                BSE SEBI Regn. No.:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">
                NSE SEBI Regn. No.:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">
                Tel.:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">
                Fax:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">
                Website :
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="4" style="font-size: 11px;font-weight: bold;text-align: left;">Whether dealing with any other stock broker/sub-broker <span style="font-size: 11px;font-weight: normal;">(in case dealing with multiple stock brokers/sub-brokers, provide details)</span></td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; font-weight: bold; border: 1px solid #000000;">
                Name of stock broker:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; font-weight: bold; border: 1px solid #000000;">
                Name of Sub-Broker/AP, if any:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left; font-weight: bold; border: 1px solid #000000;">
                Client Code:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; font-weight: bold; border: 1px solid #000000;">
                Exchange:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="2" style="font-size: 11px; text-align: left; font-weight: bold; border: 1px solid #000000;">
                Details of disputes/dues pending from/to such stock broker/sub- broker:
            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
            <td style="font-size: 11px;text-align: left; border: 1px solid #000000;">

            </td>
        </tr>
    </table>

<#-- Section H: ADDITIONAL DETAILS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="3" style="font-size: 10px;font-weight: bold;text-align: left;">H. ADDITIONAL DETAILS</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td rowspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;width: 70%;">Whether you wish to receive physical contract note or Electronic Contract Note (ECN) (please specify):</td>
            <td colspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div>
                <span style="font-size: 11px;">Electronic Contract Note</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Physical</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="3" style="font-size: 11px;text-align: left;">Specify your Email id, if applicable:</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;text-align: left;border: 1px solid #000000;">Primary email Id :</td>
                <td colspan="2" style="font-size: 11px;text-align: left;border: 1px solid #000000; text-transform: uppercase;">
                    <strong>${data.email!''}</strong>
                </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;text-align: left;border: 1px solid #000000;">Secondary email Id :</td>
            <td colspan="2" style="font-size: 11px;text-align: left;border: 1px solid #000000;"></td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td rowspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;">Whether you wish to receive Rights &amp; Obligations of stock broker, sub-broker and client as prescribed by SEBI and Stock Exchanges (including additional rights &amp; obligations in case of internet/ wireless technology based trading); Rights and Obligations of beneficial owner and depository participant as prescribed by SEBI and depositories; Uniform Risk Disclosure Documents (for all segments/ exchanges); and Guidance Note detailing Do's and Don'ts for trading on Stock Exchanges (please specify);</td>
            <td colspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Physical</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div>
                <span style="font-size: 11px;">Electronic</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px;text-align: left;border: 1px solid #000000;"> Whether you wish to avail of the facility of internet trading/ wireless technology (please specify):</td>
            <td style="font-size: 11px;text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div>
                <span style="font-size: 11px;">Yes</span>
            </td>
            <td style="font-size: 11px;text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">No</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="3" style="font-size: 11px;text-align: left;border: 1px solid #000000;">No. of years of Investment / Trading Experience: <strong>${(data.investmentExperienceYears!'')?upper_case}</strong>
</td>
        </tr>
         <tr style="border: 1px solid #000000;">
            <td colspan="3" style="font-size: 11px;text-align: left;border: 1px solid #000000;">Any other information:</td>
        </tr>
    </table>

<#-- Section I: INTRODUCER DETAILS -->

    <table class="table table-bordered PART-II-TRADING" style="margin-bottom: 5px;">
        <tr style="background: #d1d3d4;border: 1px solid #000000;">
            <td colspan="5" style="font-size: 10px;font-weight: bold;text-align: left;">I. INTRODUCER DETAILS (optional)</td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;width:25%;">
                Name of the Introducer
            </td>
            <td colspan="4" style="font-size: 11px; text-align: left;border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td rowspan="2" style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                Status of the Introducer:
            </td>
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Sub-broker</span>
            </td>
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Remisier</span>
            </td>
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Authorized Person</span>
            </td>
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Existing Client</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td colspan="4" style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div>
                <span style="font-size: 11px;">Others,</span><span style="font-size: 11px;font-weight: normal;">please specify______________________________________________________</span>
            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                Address and Tel. No. of the Introducer
            </td>
            <td colspan="4" style="font-size: 11px; text-align: left;border: 1px solid #000000;">

            </td>
        </tr>
        <tr style="border: 1px solid #000000;">
            <td style="font-size: 11px; text-align: left;border: 1px solid #000000;">
                Introducer's Signature
            </td>
            <td colspan="4" style="font-size: 11px; text-align: left;border: 1px solid #000000;">

            </td>
        </tr>
    </table>


<#-- DECLARATION -->

        <p class="instructions-header" style="font-size: 10px; text-align: center; margin-bottom: 5px;">
            DECLARATION
        </p>

        <ol class="list-font" style="padding-left: 15px; margin-bottom: 10px;">
            <li>
                I hereby declare that the details furnished above are true and correct to the best of my knowledge and belief and I undertake to
                inform you of any changes therein, immediately. In case any of the above information is found to be false or untrue or misleading
                or misrepresenting, I am aware that I may be held liable for it.
            </li>
            <li>I confirm having read/been explained and understood the contents of the document on policy and procedures of the stock broker
                nd the tariff sheet and all voluntary documents.
            </li>
            <li>I further confirm having read and understood the contents of the 'Rights and Obligations' document(s), 'Risk Disclosure Document'
               and Guidance Note/Do's &amp; Don'ts. I do hereby agree to be bound by such provisions as outlined in these documents. I have also
               been informed that the standard set of documents has been displayed for Information on stock broker's designated website:
               www.venturasecurities.com.</li>

        </ol>



    <div style="display: flex; align-items: center; font-size: 11px; padding: 10px; position:relative;">
        <div style="display: flex; flex-direction: column; gap: 0px; line-height:1.2;">
            <div><strong style="display: inline-block; width: 50px;">Place:</strong> <span>${(data.userCity!'')?upper_case}</span></div>
            <div><strong style="display: inline-block; width: 50px;">Date:</strong> <span><u><strong>${data.currentDate!''}</strong></u></span></div>
        </div>

        <p style="font-size: 11px; text-align: center; position:absolute; top:10px; right:10px; line-height:1;">
                <span>_______________________________</span><br /><br />
                Signature of the Applicant
        </p>

    </div>


<#-- FOR OFFICE USE ONLY -->

     <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px;">
        <tr>
            <th colspan="5" style="border: 1px solid black; padding: 2px; text-align: center; background: #d3d3d3; font-weight: bold;">
                FOR OFFICE USE ONLY
            </th>
        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 2px; text-align: center; font-weight: bold;">
                I wish to open
            </td>
            <td style="border: 1px solid black; padding: 2px; text-align: center;">
                 <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div> <br /> Equity &amp; Commodity Trading &amp; DP
            </td>
            <td style="border: 1px solid black; padding: 2px; text-align: center;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div> <br />Only Equity Trading
            </td>
            <td style="border: 1px solid black; padding: 2px; text-align: center;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div> <br />Only DP
            </td>
            <td style="border: 1px solid black; padding: 2px; text-align: center;">
                <div class="square-box" style="width: 10px; height: 10px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"></div> <br />Only Commodity Trading
            </td>
        </tr>
    </table>



</div>

<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 09 -------- </span>

<#-- KYC Form Part 3 - Converted from Laravel Blade (lines 5790-8934) to FreeMarker -->
<#-- FORM-9 Demat Account Opening, Nominations, DP Charges, DDPI, FATCA, MITC, etc. -->
<#-- All @php blocks removed - logic moved to Java service layer -->

<#assign checkmark = '<img src="data:image/png;base64,${data.checkmarkBase64!""}" style="width:10px;height:10px;" />'>

<div class="WordSectionneww100">
    <div style="text-align: right;">
        <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
    </div>
    <div>
        <!--<p style="font-size: 11px;text-align: center;">PART II - TRADING ACCOUNT RELATED DETAILS</p>-->
    </div>


   <table style="width: 100%; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 11px; border:none;">
    <tr style="border:none;">

        <td style="width: 35%; vertical-align: top; padding-top: -80px; border:none;">
            <table style="width: 100%; border-collapse: collapse; PART-II-TRADING">
                <tr>
                    <td style="border: 1px solid black; padding: 2px; font-weight: bold; width: 50%;">Branch Code</td>
                    <td style="border: 1px solid black; padding: 2px; width: 50%;"> </td>
                </tr>
                <tr>
                    <td style="border: 1px solid black; padding: 2px; font-weight: bold;">Scheme Code</td>
                    <td style="border: 1px solid black; padding: 2px;"> </td>
                </tr>
                <tr>
                    <td style="border: 1px solid black; padding: 2px; font-weight: bold;">Trading Code</td>
                    <td style="border: 1px solid black; padding: 2px;"> </td>
                </tr>
            </table>
        </td>

        <td style="width: 35%; text-align: center; font-weight: bold; font-size:14px; vertical-align: bottom; vertical-align: bottom; padding-top: 80px;border:none;">
            FORM-9<br />
            DP ID - IN303116
        </td>


        <td style="width: 30%; text-align: right; border:none;">
            <table style="border: 1px solid black;  margin-left: auto; width: 100%; vertical-align: bottom; vertical-align: bottom; padding-top: 80px;">
                <tr>
                    <td style="padding: 5px; font-weight: bold; text-align: left;">FU</td>
                </tr>
            </table>
        </td>
    </tr>
</table>


 <table style="width: 100%; border-collapse: collapse; font-size: 11px; border:none;">
        <tr style="border:none;">
            <th colspan="10" style=" background: #d3d3d3; font-size: 11px; text-align: center;  padding: 5px;border:none;">
                PART II - DEMAT ACCOUNT OPENING FORM <i>(FOR INDIVIDUALS)</i>
            </th>
        </tr>

        <tr style="border:none;">
            <td colspan="10" style="height: 1px; border: none; border:none;"> </td>
        </tr>


        <tr>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold; width: 30%;">Date : <strong>${data.currentDate!''}</strong></td>
            <!--<td style="border: 1px solid black; padding: 5px; width: 20%;"></td>-->
            <td style="border: 1px solid black; padding: 5px; font-weight: bold; width: 30%;">Client Id <span style="font-weight: normal;">(To be filled by Participant)</span></td>

            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
            <td style="border: 1px solid black; padding: 5px; width: 5%;"> </td>
        </tr>

        <tr style="border:none;">
            <td colspan="10" style=" padding: 5px; border:none;">
                I / We request you to open a depository account in my/our name as per the following details:<br />
                (Please fill all the details in <strong>CAPITAL LETTERS</strong> only)
            </td>
        </tr>
    </table>



    <table style="width: 100%; border-collapse: collapse;  font-size: 11px; border: 1px solid black;">

   <tr>
    <th colspan="6" style="width:100%; padding: 5px; text-align: left;">
        Details of Account holder (s):
    </th>
</tr>

    <tr>
        <th colspan="2" style="border: 1px solid black; padding: 5px; text-align: left; width: 20%;"> </th>
        <th style="border: 1px solid black; padding: 5px; width: 30%;">NAME</th>
        <th style="border: 1px solid black; padding: 5px; width: 30%;">PAN</th>
        <th colspan="2" style="border: 1px solid black; padding: 5px; width: 20%;">SMS Alert Facility</th>
    </tr>


    <tr>
        <td colspan="4" style="border: 1px solid black; padding: 5px;"> </td>
        <td style="border: 1px solid black; padding: 5px;">Yes</td>
        <td style="border: 1px solid black; padding: 5px;">No</td>
    </tr>


    <tr>
        <td colspan="2" style="border: 1px solid black; padding: 5px; font-weight: bold;">Sole / First Holder</td>
        <td style="border: 1px solid black; padding: 5px;"><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></td>
        <td style="border: 1px solid black; padding: 5px;"><strong>${(data.taxPanNo!'')?upper_case}</strong>
        </td>
        <td style=" border: 1px solid black;  padding: 5px; "><div class="square-box" style="width: 25px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div></td>
        <td style="border: 1px solid black; padding: 5px;"><div class="square-box" style="width: 25px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div></td>
    </tr>


   <tr>
<td colspan="2" style="border: 1px solid black; padding: 5px; font-weight: bold;">
Occupation ( please tick any one and give brief details):
</td>

<td colspan="4" style="border: 1px solid black; ">

<!-- Private Sector -->
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Salaried - Pvt Sector'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: top;">Private Sector</span> &nbsp;

<!-- Public Sector -->
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Salaried - Public Sector'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: top;">Public Sector</span> &nbsp;

<!-- Government Service -->
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Salaried - GOVT Service'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: top;">Government Service</span> &nbsp;

<!-- Business -->
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Business'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: top;">Business</span> &nbsp;

<!-- Student -->
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Student'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: top;">Student</span>

<br />

<!-- Professional -->
<div style="width:12px;height:12px;border:1px solid #000;margin-top:5px;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Professional'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: center;">Professional</span> &nbsp;

<!-- Agriculturist -->
<div style="width:12px;height:12px;border:1px solid #000;margin-top:5px;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Agriculturist'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: center;">Agriculturist</span> &nbsp;

<!-- Retired -->
<div style="width:12px;height:12px;border:1px solid #000;margin-top:5px;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Retired'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: center;">Retired</span> &nbsp;

<!-- Housewife -->
<div style="width:12px;height:12px;border:1px solid #000;margin-top:5px;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Housewife'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: center;">Housewife</span> &nbsp;

<!-- Others -->
<div style="width:12px;height:12px;border:1px solid #000;margin-top:5px;display:inline-block;text-align:center;">
<#if (data.occupation!'') == 'Others'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
<span style="vertical-align: center;">Others</span>

<table style="width: 100%; border-collapse: collapse; border-top: 1px solid black; border-left:none; border-right:none; border-bottom:none; margin-top:5px ">
<tr>
<td style="border:none;">${(data.natureOfOrganisation!'NA')?upper_case}</td>
</tr>
</table>

</td>
</tr>




     <tr>
        <td colspan="2" style="border: 1px solid black; padding: 5px; font-weight: bold;">Second Holder</td>
        <td style="border: 1px solid black; padding: 5px;"> </td>
        <td style="border: 1px solid black; padding: 5px;">
        </td>
        <td style=" border: 1px solid black;  padding: 5px; "><div class="square-box" style="width: 25px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div></td>
        <td style="border: 1px solid black; padding: 5px;"><div class="square-box" style="width: 25px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div></td>
    </tr>


    <tr>
        <td colspan="2" style="border: 1px solid black; padding: 5px; font-weight: bold;">Occupation (please tick any one and give brief details):</td>
        <td colspan="4" style="border: 1px solid black; ">
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;">Private Sector</span> &nbsp;
          <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;"> Public Sector </span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;">Government Service</span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;">Business</span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;"> Student</span> &nbsp;

            <br />
            <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Professional</span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px;  margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Agriculturist</span> &nbsp;
           <div class="square-box" style="width: 12px; height: 12px;   margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Retired</span>&nbsp;
           <div class="square-box" style="width: 12px; height: 12px;   margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Housewife </span>&nbsp;
            <div class="square-box" style="width: 12px; height: 12px;   margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Others</span>
             <table style="width: 100%; border-collapse: collapse; border-top: 1px solid black;  border-left:none; border-right:none; border-bottom:none; margin-top:5px ">
                <tr style="border:none;">
                  <td style="border:none;"> </td>
                 </tr>
             </table>
        </td>
    </tr>


     <tr>
        <td colspan="2" style="border: 1px solid black; padding: 5px; font-weight: bold;">Third Holder</td>
        <td style="border: 1px solid black; padding: 5px;"> </td>
        <td style="border: 1px solid black; padding: 5px;">
        </td>
        <td style=" border: 1px solid black;  padding: 5px; "><div class="square-box" style="width: 25px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div></td>
        <td style="border: 1px solid black; padding: 5px;"><div class="square-box" style="width: 25px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div></td>
    </tr>


    <tr>
        <td colspan="2" style="border: 1px solid black; padding: 5px; font-weight: bold;">Occupation (please tick any one and give brief details):</td>
        <td colspan="4" style="border: 1px solid black; ">
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;">Private Sector</span> &nbsp;
          <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;"> Public Sector </span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;">Government Service</span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;">Business</span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: top;"> Student</span> &nbsp;

            <br />
            <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Professional</span> &nbsp;
            <div class="square-box" style="width: 12px; height: 12px;  margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Agriculturist</span> &nbsp;
           <div class="square-box" style="width: 12px; height: 12px;   margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Retired</span>&nbsp;
           <div class="square-box" style="width: 12px; height: 12px;   margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Housewife </span>&nbsp;
            <div class="square-box" style="width: 12px; height: 12px;   margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> <span style="vertical-align: center;">Others</span>
             <table style="width: 100%; border-collapse: collapse; border-top: 1px solid black;  border-left:none; border-right:none; border-bottom:none; margin-top:5px ">
                <tr style="border:none;">
                  <td style="border:none;"> </td>
                 </tr>
             </table>
        </td>
    </tr>



</table>




<table style="width: 100%; border-spacing: 0; font-size: 11px; border: 1px solid black; table-layout: fixed; margin-top:3px;">
    <tr>
        <th colspan="6" style="text-align: left; border: 1px solid black; padding: 5px;">
            For Association of Persons (AOP), Partnership Firm, Unregistered Trust, etc., although the account is opened in the name of the natural persons, the name &amp; PAN of the Association of persons (AOP), Partnership Firm, Unregistered Trust etc., should be mentioned below:
        </th>
    </tr>
    <tr>
        <td style="width: 15%; font-weight: bold; border: 1px solid black; padding: 5px;">Name</td>
        <td colspan="2" style="border: 1px solid black; padding: 5px; width: 35%;"> </td>
        <td style="width: 15%; font-weight: bold; border: 1px solid black; padding: 5px;">PAN</td>
        <td colspan="2" style="border: 1px solid black; padding: 5px; width: 35%;"> </td>
    </tr>
</table>


<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:15px;">
        <tr>
            <th style="text-align: left; border: 1px solid black; padding: 5px; background-color: #d1d1d1;">
                Type of Account
            </th>
        </tr>
        <tr colspan="6">
            <td style="border: 1px solid black; border-bottom:none; padding: 5px; width:100%; padding-top:15px;">
                <label style="width:16.66%;"><div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> Ordinary Resident</label>
                <label style=" width:16.66%; "><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div> NRI-Repatriable</label>
                <label style=" width:16.66%;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> Margin</label>
                <label style=" width:16.66%;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> Foreign National</label><br />
                <label style=" width:25%;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> Others <i>(please specify)</i></label>
                <span style="display: inline-block; width: 150px; border-bottom: 1px solid black; "> </span>
            </td>
         </tr>
          <tr colspan="6">
                <td style="border: 1px solid black; border-bottom:none; padding: 5px; padding-top:15px;">
                <label><div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> NRI-Non-Repatriable</label>
                <label style="margin-left: 20px;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> Promoter</label>
                <label style="margin-left: 20px;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase;"> </div> Qualified Foreign Investor</label>
                <span style="display: inline-block; width: 200px; border-bottom: 1px solid black;"> </span>
            </td>
        </tr>
    </table>



<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:3px;">

    <tr>
        <th colspan="6" style="text-align: left; border: 1px solid black; padding: 5px; background-color: #d1d1d1;">
            Gross Annual Income Details
        </th>
    </tr>

    <tr>
        <td colspan="6" style="border: 1px solid black; padding: 5px;">
            Income Range per annum (please tick any one) (Rs. in lacs)
        </td>
    </tr>

<tr style="width:100%;">

<td style="border: 1px solid black; width:20%; padding: 1px; text-align: center;">
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;">
<#if (data.grossIncome!'') == '100000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
Below 1
</td>

<td style="border: 1px solid black; width:20%; padding: 1px; text-align: center;">
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;">
<#if (data.grossIncome!'') == '500000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
1 to 5
</td>

<td style="border: 1px solid black; width:20%; padding: 1px; text-align: center;">
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;">
<#if (data.grossIncome!'') == '1000000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
5 to 10
</td>

<td style="border: 1px solid black; width:20%; padding: 1px; text-align: center;">
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;">
<#if (data.grossIncome!'') == '1500000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
10 to 25
</td>

<td colspan="2" style="border: 1px solid black; width:20%; padding: 1px; text-align: center;">
<div style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;">
<#if (data.grossIncome!'') == '2000000'>
<img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
</#if>
</div>
Above 25
</td>

</tr></table>

<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:3px;">

    <tr style="width:100% padding:-3%;">
        <td colspan="2" style="  text-align: center;"><strong> Please tick, if applicable: </strong></td>
        <td colspan="2" style="  text-align: center;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:2px;"> </div> Politically Exposed Person (PEP)</td>
        <td colspan="2" style="  text-align: center;"><div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:2px;"> </div> Related to a Politically Exposed Person (PEP)</td>

    </tr>
</table>

<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:3px;">
    <tr style="background-color: #d1d1d1;">
        <th colspan="10" style="border: 1px solid black; padding: 5px; text-align: left;">In case of NRIs / Foreign Nationals</th>
    </tr>

<tr>
<td style="border: 1px solid black; padding: 5px; width: 20%;">RBI Approval Reference No.</td>

<td style="border: 1px solid black; padding: 5px; width: 30%;">
${(data.rbiApprovalNumber!'')?upper_case}
</td>

<td style="border: 1px solid black; padding: 5px; width: 20%;">RBI Approval Date</td>

<td style="border:none; padding: 0px; width: 30%;">
<table style="width: 100%; border-collapse: collapse; border:none;">

<tr style="border:none;">
<#assign rbiDateStr = data.rbiApprovalDateDmY!''>
<#list 0..<8 as i>
<td style="border-top:none; text-align:center; width:12.5%; height:12px;">
<#if (i < rbiDateStr?length)>${rbiDateStr[i..i]}</#if>
</td>
</#list>
</tr>

<tr>
<td style="border-bottom:none;border-right:none;text-align:center;height:12px;">D</td>
<td style="border-bottom:none;text-align:center;height:12px;">D</td>
<td style="border-bottom:none;text-align:center;height:12px;">M</td>
<td style="border-bottom:none;text-align:center;height:12px;">M</td>
<td style="border-bottom:none;text-align:center;height:12px;">Y</td>
<td style="border-bottom:none;text-align:center;height:12px;">Y</td>
<td style="border-bottom:none;text-align:center;height:12px;">Y</td>
<td style="border-bottom:none;text-align:center;height:12px;">Y</td>
</tr>

</table>
</td>
</tr></table>

<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 10 -------- </span>
 <div class="page-break"> </div>
<br />
<table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top: 3px; border: 1px solid black;">
    <tr>
        <th colspan="6" style="background-color: #d1d1d1; text-align: left; padding: 5px; border: 1px solid black;">Bank Account Details</th>
    </tr>
    <tr>
        <td colspan="6" style="font-weight: bold; padding: 5px; border: 1px solid black;">Bank Name:-   ${data.bankName!''}</td>
    </tr>
    <tr>
        <td colspan="6" style="padding: 5px; border: 1px solid black;">Branch Address:-   ${data.bankBranchAddress!''}</td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black; width:10%;">City/Town</td>
        <td colspan="3" style="padding: 5px; border: 1px solid black; width: 40%;">
    ${(data.bankDetailsCity!'')?upper_case}
</td>
        <td style="font-weight: bold; padding: 5px; border: 1px solid black; width:20%;">Pincode</td>
        <td style="border: 1px solid black; width:30%;">
    <table style="border-collapse: collapse; margin: auto;">
        <tr>
            <#assign zipVal = data.bankDetailsZipCode!''>
            <#list 0..<6 as i>
                <td style="width: 12px; height: 20px; border: 1px solid black; text-align:center;">
                    <#if (i < zipVal?length)>${zipVal[i..i]}</#if>
                </td>
            </#list>
        </tr>
    </table>
</td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black;">State</td>
<td colspan="3" style="border: 1px solid black;">
    ${(data.bankDetailsStateName!'')?upper_case}
</td>

<td style="font-weight: bold; padding: 5px; border: 1px solid black;">Country</td>
<td style="border: 1px solid black;">
    ${data.bankDetailsCountryName!'INDIA'}
</td>
    </tr>
    <tr>
        <td colspan="6" style="padding: 5px; border: 1px solid black;">Bank Account No.:-  ${data.bankAccountNo!''}</td>
    </tr>
    <tr>
        <td style="font-weight: bold; padding: 5px; border: 1px solid black;">Account Type</td>
        <td colspan="5" style="padding: 5px; border: 1px solid black;">

            <div class="square-box" style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;"> </div>
            Savings &nbsp;&nbsp;

            <div class="square-box" style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;"> </div>
            Current &nbsp;&nbsp;

            <div class="square-box" style="width:12px;height:12px;border:1px solid #000;display:inline-block;position:relative;top:3px;">
                <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="width:10px;height:10px;" />
            </div>
            Others <span style="font-style: italic;">(Please specify)</span>

        </td>
    </tr>

    <tr>

    <td style="font-weight: bold; padding: 5px; border: 1px solid black; width:10%;">
        MICR No.
    </td>

    <td colspan="3" style="border: 1px solid black; width:30%;">
        <table style="border-collapse: collapse; margin: auto; width:100%;">
            <tr>
                <#assign micrVal = data.bankDetailsMicr!''>
                <#list 0..<10 as i>
                    <td style="width:10%; height: 20px; border: 1px solid black; text-align:center;">
                        <#if (i < micrVal?length)>${micrVal[i..i]}</#if>
                    </td>
                </#list>
            </tr>
        </table>
    </td>

        <td style="font-weight: bold; padding: 5px; border: 1px solid black; width:10%;">IFSC Code</td>

        <td style="border: 1px solid black; width:30%;">
            <table style="border-collapse: collapse; margin: auto; width:100%;">
                <tr>
                    <#assign ifscVal = data.ifscCode!''>
                    <#list 0..<11 as i>
                        <td style="width:9.09%; height:14px; border:1px solid black; text-align:center; font-size:11px;">
                            <#if (i < ifscVal?length)>${ifscVal[i..i]}</#if>
                        </td>
                    </#list>
                </tr>
            </table>
        </td>

    </tr>
    <tr>
        <td colspan="6" style="font-style: italic; padding: 5px; border: 1px solid black;">Please provide cancelled cheque leaf for MICR &amp; IFSC Code</td>
    </tr>
</table>

<br />
<br />

            <table style="border-collapse: collapse; margin: auto; width:100%; border:none;">
                <tr style="width:100%; border:none;">
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center;  line-height:1;">
                            <span>_______________________________</span><br /><br />
                             <b>   Signature of the 1st Holder</b>
                         </p>
                    </td>
                    <td style="border:none;">
                       <p style="font-size: 11px; text-align: center;  line-height:1;">
                            <span>_______________________________</span><br /><br />
                              <b>  Signature of the 2nd Holder</b>
                       </p>
                    </td>
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center;  line-height:1;">
                            <span>_______________________________</span><br /><br />
                                <b>Signature of the 3rd Holder</b>
                         </p>

                    </td>
                </tr>
            </table>

	 <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 11 -------- </span>
<br />
        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align: left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>
        <div>
            <!--<p style="font-size: 11px;text-align: center;">PART II - TRADING ACCOUNT RELATED DETAILS</p>-->
        </div>



    <table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black;">
    <!-- Header Row -->
    <tr>
        <th colspan="3" style="background-color: #d1d1d1; text-align: left;  padding: 5px; border: 1px solid black;">Standing Instructions</th>
    </tr>

    <!-- Instruction Rows -->
   <tr>
    <td style="padding: 5px; border: 1px solid black; width: 60%; font-weight:bold;">I/We authorize you to receive credits automatically into my/our account</td>
    <td style="text-align: center; padding: 5px; border: 1px solid black; width: 20%;">
        <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; position: relative; top: 3px; border: 1px solid black;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div>
        Yes
    </td>
    <td style="text-align: center; padding: 5px; border: 1px solid black; width: 20%;">
        <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; position: relative; top: 3px; border: 1px solid black;"> </div>
        No
    </td>
</tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black;  font-weight:bold;">Account to be operated through Power of Attorney (PoA)</td>
        <td style="text-align: center; padding: 5px; border: 1px solid black;">
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div> Yes
        </td>
        <td style="text-align: center; padding: 5px; border: 1px solid black;">
          <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div>
            No
        </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black;  font-weight:bold;">Account to be operated through Demat Debit and Pledge Instruction (DDPI)</td>
        <td style="text-align: center; padding: 5px; border: 1px solid black;">
           <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div> Yes
        </td>
        <td style="text-align: center; padding: 5px; border: 1px solid black;">
          <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div>
            No
        </td>
    </tr>

    <!-- SMS Alert Row -->
    <tr>
        <td colspan="3" style="padding: 5px; border: 1px solid black; font-size: 11px;">
            <strong>SMS Alert facility :</strong> <span style="font-style: italic;">[Mandatory if you are giving Power of Attorney (PoA/DDPI). Ensure that the mobile number is provided in the KYC Application Form]</span>
        </td>
    </tr>

    <!-- Mode of Receiving Documents -->
    <tr>
        <td style="padding: 5px; border: 1px solid black;">
            <strong>Mode of receiving Statement of Account, Rights &amp; Obligations Documents</strong> <span style="font-style: italic;">(Tick any one)</span>
        </td>
        <td colspan="2" style="text-align: left; padding: 5px; border: 1px solid black;">
           <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div> <b>Physical Form</b>
        <br />
           <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div> <b>Electronic Form</b>
            <span style="font-style: italic; font-size: 11px;">(Read Note 4 and ensure that email ID is provided in KYC Application Form)</span>
        </td>
    </tr>

    <!-- Joint Account Communication -->
    <tr>
        <td style="padding: 5px; border: 1px solid black;">
            <strong>For Joint accounts, communication to be sent to</strong>
        </td>
        <td colspan="2" style="text-align: left; padding: 5px; border: 1px solid black;">
            <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div> <b>First holder</b>
           <div class="square-box" style="width: 12px; height: 12px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div> <b>All joint account holders</b>
        </td>
    </tr>
</table>


<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:3px;">
    <tr>
        <th colspan="2" style="background-color: #d1d1d1; text-align: left; padding: 5px; border: 1px solid black;">
            Mode of Operations for Joint Accounts
        </th>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black; width: 50%;">
            <div style="display: inline-block; width: 12px; height: 12px; border: 1px solid black; margin-right: 5px;"> </div>
            Jointly
        </td>
        <td style="padding: 5px; border: 1px solid black; width: 50%;">
            <div style="display: inline-block; width: 12px; height: 12px; border: 1px solid black; margin-right: 5px;"> </div>
            Anyone of the holder or survivor(s)
        </td>
    </tr>
    <tr>
        <td colspan="2" style="padding: 5px; border: 1px solid black; font-size: 11px;">
            If Mode of Operation for Joint Account is chosen as anyone of the holder or survivor(s), only specified operations such as transfer of securities including Inter-Depository Transfer, pledge / hypothecation / margin pledge / margin re-pledge (creation, closure and invocation and confirmation thereof as applicable) of securities and freeze/unfreeze of account and/or securities and/or specific number of securities will be permitted.
        </td>
    </tr>
</table>


<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:3px;">
    <tr>
        <th colspan="2" style="background-color: #d1d1d1; text-align: left; padding: 5px; border: 1px solid black;">
            Guardian Details (where sole holder is a minor)
        </th>
    </tr>
    <tr>
        <td colspan="2" style="padding: 5px; font-size: 11px; font-style: italic; border: 1px solid black;">
            <b>(For account of a minor, two KYC Application Forms must be filled i.e. one for the guardian and another for the minor (to be signed by guardian))</b>
        </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black; width: 30%;"><b>Guardian Name</b></td>
        <td style="padding: 5px; border: 1px solid black; width: 70%;"> </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black;"><b>Relationship of Guardian with minor</b></td>
        <td style="padding: 5px; border: 1px solid black;"> </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black;"><b>PAN</b></td>
        <td style="padding: 5px; border: 1px solid black;">
            <table style="width: 100%; border-collapse: collapse;">
                <tr>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                    <td style="width: 10%; height: 15px; border: 1px solid black;"> </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:5px; border:none;">
    <tr style="border:none;">
        <th colspan="2" style="background-color: #d1d1d1; text-align: left; padding: 5px; text-align: center; border:none;">
            DECLARATION
        </th>
    </tr>
    <tr style="border:none;">
        <td colspan="2" style="padding: 5px; font-size: 11px; text-align: justify; border:none;">
            The rules and regulations of the Depository and Depository Participants pertaining to an account which are in force now have been read by me/us and I/we have
understood the same and I/we agree to abide by and to be bound by the rules as are in force from time to time for such accounts. I/we hereby declare that the details
furnished above are true and correct to the best of my/our knowledge and belief and I/we undertake to inform you of any changes therein, immediately. In case any
of the above information is found to be false or untrue or misleading or misrepresenting. I am / we are aware that I/we may be held liable for it. In case non-resident
account, I/we also declare that I/we have complied and will continue to comply with FEMA regulations. I/we acknowledge the receipt of the copy of the document,
"Rights and Obligations of the Beneficial Owner and Depository Participant."
        </td>
    </tr>

</table>


<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black; margin-top:5px; ">
    <tr style="background-color: #d1d1d1; text-align: center;">
        <th style="border: 1px solid black; width: 40%;"> </th>
        <th style="border: 1px solid black; width: 30%;">Name(s) of holder(s)</th>
        <th style="border: 1px solid black; width: 30%;">Signature(s) of holder</th>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black; font-weight: bold;">
            Sole / First Holder / Guardian<br />
            <span style="font-style: italic;">(in case sole holder is minor)</span> (Mr./Ms.)
        </td>
        <td style="padding: 5px; border: 1px solid black;"><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></td>
        <td style="padding: 5px; border: 1px solid black; font-weight: bold;">
        </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black; font-weight: bold;">Second Holder (Mr./Ms.)</td>
        <td style="padding: 5px; border: 1px solid black;"> </td>
        <td style="padding: 5px; border: 1px solid black; font-weight: bold;">
        </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid black; font-weight: bold;">Third Holder (Mr./Ms.)</td>
        <td style="padding: 5px; border: 1px solid black;"> </td>
        <td style="padding: 5px; border: 1px solid black; font-weight: bold;">
        </td>
    </tr>
</table>
<br />
	 <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 12 -------- </span>
      <div class="page-break"> </div>
<br />
    <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:5px; border:none;">
    <tr style="border:none;">
        <th colspan="2" style="background-color: #d1d1d1; text-align: left; padding: 5px; text-align: center; border:none;">
            NOMINATION FOR TRADING AND DEMAT ACCOUNT
        </th>
    </tr>
    <tr style="border:none;">
        <td colspan="2" style="padding: 5px; font-size: 11px; text-align: center; border:none;">
           <b> NOMINATION FORM - Annexure - A</b>
        </td>
    </tr>

</table>



 <table style="width: 100%; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 11px; border: 1px solid black;">
        <!-- Nomination Heading -->
        <tr>
            <td colspan="4" style="border: 1px solid black; padding: 5px;">
                I/We wish to make a nomination. [As per details given below]
            </td>
        </tr>
        <!-- Nomination Details -->
        <tr>
            <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold;">Nomination Details</td>
        </tr>
        <tr>
            <td colspan="4" style="border: 1px solid black; padding: 5px;">
                I/We wish to make a nomination and do hereby nominate the following person(s) who shall receive all the assets held in my / our account in the event of my / our death.
            </td>
        </tr>
        <!-- Nominee Details Heading -->
        <tr>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;" colspan="1">Nomination can be made up to three nominees in the account.</td>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Details of 1st Nominee</td>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Details of 2nd Nominee</td>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Details of 3rd Nominee</td>
        </tr>
        <!-- Mandatory Details -->
        <tr>
            <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold;">Mandatory Details</td>
        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">1. Name of the nominee(s) (Mr./Ms.)</td>
           <td style="border: 1px solid black; padding: 5px;">
                <strong><#if (data.nominee1Name!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee1Name!'')?upper_case}</#if></strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong><#if (data.nominee2Name!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee2Name!'')?upper_case}</#if></strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong><#if (data.nominee3Name!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee3Name!'')?upper_case}</#if></strong>
            </td>
        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px;">
              <table style="width: 100%; border-collapse: collapse; border:none;">
               <tr style="border:none;">
                   <td style="padding: 0%; font-weight: bold; width: 50%; border:none;">2. Share of each Nominee</td>
                   <td style="padding: 0%; width: 50%; border:none;">Equally [If not equally, please specify percentage]  </td>
               </tr>
             </table>
            </td>

            <td style="border: 1px solid black; padding: 5px; text-align: center;">
                <strong>
                    <#if (data.nominee1Share!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee1Share!'')?upper_case}%</#if>
                </strong>
            </td>

            <td style="border: 1px solid black; padding: 5px; text-align: center;">
                <strong>
                    <#if (data.nominee2Share!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee2Share!'')?upper_case}%</#if>
                </strong>
            </td>

            <td style="border: 1px solid black; padding: 5px; text-align: center;">
                <strong>
                    <#if (data.nominee3Share!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee3Share!'')?upper_case}%</#if>
                </strong>
            </td>


        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px;" colspan="4">Any odd lot after division shall be transferred to the first nominee mentioned in the form.</td>
        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px;"><b> 3. Relationship With the Applicant (If Any)</b></td>
            <td style="border: 1px solid black; padding: 5px;">
                <strong>
                    <#if (data.appointNominee!'') != 'no'>
                        ${(data.nominee1Relation!'')?upper_case}
                    </#if>
                </strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong>
                    <#if (data.appointNominee!'') != 'no'>
                        ${(data.nominee2Relation!'')?upper_case}
                    </#if>
                </strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong>
                    <#if (data.appointNominee!'') != 'no'>
                        ${(data.nominee3Relation!'')?upper_case}
                    </#if>
                </strong>
            </td>


        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px;"> <b>4. Date of Birth </b><br /> {in case of minor nominee(s)} </td>
            <td style="border: 1px solid black; padding: 5px;">
                <strong>
                    <#if (data.nominee1Dob!'') != '' && (data.appointNominee!'') != 'no'>${data.nominee1Dob!''}</#if>
                </strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong>
                    <#if (data.nominee2Dob!'') != '' && (data.appointNominee!'') != 'no'>${data.nominee2Dob!''}</#if>
                </strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong>
                    <#if (data.nominee3Dob!'') != '' && (data.appointNominee!'') != 'no'>${data.nominee3Dob!''}</#if>
                </strong>
            </td>

        </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px;"> <b>5. Name of Guardian (Mr./Ms.)</b> <br /> {in case of minor nominee(s)}</td>
            <td style="border: 1px solid black; padding: 5px;">
                <strong><#if (data.guardian1!'') != '' && (data.appointNominee!'') != 'no'>${(data.guardian1!'')?upper_case}</#if></strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong><#if (data.guardian2!'') != '' && (data.appointNominee!'') != 'no'>${(data.guardian2!'')?upper_case}</#if></strong>
            </td>

            <td style="border: 1px solid black; padding: 5px;">
                <strong><#if (data.guardian3!'') != '' && (data.appointNominee!'') != 'no'>${(data.guardian3!'')?upper_case}</#if></strong>
            </td>
        </tr>
    </table>


	 <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 13 -------- </span>

        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align: right;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>
        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>



<table style="width: 100%; border-collapse: collapse; font-size: 11px; border: 1px solid black;">

    <tr>
        <td colspan="4" style="border: 1px solid black; font-weight: bold; padding: 5px; width: 100%; ">Non-mandatory Details</td>
    </tr>


    <tr>
        <td style="border: 1px solid black; font-weight: bold; padding: 5px; width: 40%;">6. Address of Nominee(s)/Guardian in case of Minor</td>

            <td style="border: 1px solid black; padding: 5px; width: 20%;">
                <#if (data.appointNominee!'') != 'no'>
                    <strong>${(data.nominee1Address!'')?upper_case}</strong>
                </#if>
            </td>
            <td style="border: 1px solid black; padding: 5px; width: 20%;">
                <#if (data.appointNominee!'') != 'no'>
                    <strong>${(data.nominee2Address!'')?upper_case}</strong>
                </#if>
            </td>
            <td style="border: 1px solid black; padding: 5px; width: 20%;">
                <#if (data.appointNominee!'') != 'no'>
                    <strong>${(data.nominee3Address!'')?upper_case}</strong>
                </#if>
            </td>
    </tr>


    <tr>
    <td style="border: 1px solid black; padding: 5px; width: 40%;">City / Place:</td>

        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>${(data.nomineeCity1!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>${(data.nomineeCity2!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>${(data.nomineeCity3!'')?upper_case}</strong>
            </#if>
        </td>
</tr>

    <tr>
    <td style="border: 1px solid black; padding: 5px; width: 40%;">State &amp; Country:</td>

        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>
                    ${(data.nomineeStateName1!'')?upper_case}<#if (data.nomineeStateName1!'') != '' && (data.nomineeCountryName1!'') != ''>, </#if>${(data.nomineeCountryName1!'')?upper_case}
                </strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>
                    ${(data.nomineeStateName2!'')?upper_case}<#if (data.nomineeStateName2!'') != '' && (data.nomineeCountryName2!'') != ''>, </#if>${(data.nomineeCountryName2!'')?upper_case}
                </strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>
                    ${(data.nomineeStateName3!'')?upper_case}<#if (data.nomineeStateName3!'') != '' && (data.nomineeCountryName3!'') != ''>, </#if>${(data.nomineeCountryName3!'')?upper_case}
                </strong>
            </#if>
        </td>
</tr>

    <tr>
    <td style="border: 1px solid black; padding: 5px; width: 40%; font-weight:bold;">
        Pincode :
    </td>

        <td style="border: 1px solid black; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>${(data.nomineePincode1!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>${(data.nomineePincode2!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
                <strong>${(data.nomineePincode3!'')?upper_case}</strong>
            </#if>
        </td>
</tr>


    <tr>
        <td style="border: 1px solid black; padding: 5px;  font-weight:bold;  width: 40%;">7. Mobile / Telephone No. of nominee(s) / Guardian in case of Minor </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <strong><#if (data.nominee1Mobile!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee1Mobile!'')?upper_case}</#if></strong>
        </td>

        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <strong><#if (data.nominee2Mobile!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee2Mobile!'')?upper_case}</#if></strong>
        </td>

        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <strong><#if (data.nominee3Mobile!'') != '' && (data.appointNominee!'') != 'no'>${(data.nominee3Mobile!'')?upper_case}</#if></strong>
        </td>
    </tr>



<tr>
    <td style="border: 1px solid black; padding: 5px; font-weight:bold; width: 40%;">8. Email ID of nominee(s) / Guardian in case of Minor</td>

        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
            <strong>${(data.nominee1Email!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
            <strong>${(data.nominee2Email!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;">
            <#if (data.appointNominee!'') != 'no'>
            <strong>${(data.nominee3Email!'')?upper_case}</strong>
            </#if>
        </td>
</tr>


<tr>
<td style="border: 1px solid black; padding: 5px; width: 40%;">
<strong>
9. Nominee Identification details - [Please tick any one of following and provide details of same]
</strong>
<br />

<div class="square-box" style="width:12px;height:12px;display:inline-block;position:relative;top:3px;">
    <#if (data.nomineeDocType1!'')?lower_case == 'photograph' || (data.nomineeDocType2!'')?lower_case == 'photograph' || (data.nomineeDocType3!'')?lower_case == 'photograph'>
        <#if (data.appointNominee!'') != 'no'><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" /></#if>
    </#if>
</div> Photograph &amp; Signature

<div class="square-box" style="width:12px;height:12px;display:inline-block;position:relative;top:3px;">
    <#if (data.nomineeDocType1!'')?lower_case == 'pan' || (data.nomineeDocType2!'')?lower_case == 'pan' || (data.nomineeDocType3!'')?lower_case == 'pan'>
        <#if (data.appointNominee!'') != 'no'><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" /></#if>
    </#if>
</div> PAN

<br />

<div class="square-box" style="width:12px;height:12px;display:inline-block;position:relative;top:3px;">
    <#if (data.nomineeDocType1!'')?lower_case == 'aadhaar' || (data.nomineeDocType2!'')?lower_case == 'aadhaar' || (data.nomineeDocType3!'')?lower_case == 'aadhaar'>
        <#if (data.appointNominee!'') != 'no'><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" /></#if>
    </#if>
</div> Aadhaar

<div class="square-box" style="width:12px;height:12px;display:inline-block;position:relative;top:3px;">
    <#if (data.nomineeDocType1!'')?lower_case == 'saving bank account no.' || (data.nomineeDocType2!'')?lower_case == 'saving bank account no.' || (data.nomineeDocType3!'')?lower_case == 'saving bank account no.'>
        <#if (data.appointNominee!'') != 'no'><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" /></#if>
    </#if>
</div> Saving Bank account no.

<br />

<div class="square-box" style="width:12px;height:12px;display:inline-block;position:relative;top:3px;">
    <#if (data.nomineeDocType1!'')?lower_case == 'passport' || (data.nomineeDocType2!'')?lower_case == 'passport' || (data.nomineeDocType3!'')?lower_case == 'passport'>
        <#if (data.appointNominee!'') != 'no'><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" /></#if>
    </#if>
</div> Proof of Identity

<div class="square-box" style="width:12px;height:12px;display:inline-block;position:relative;top:3px;">
    <#if (data.nomineeDocType1!'')?lower_case == 'demat account id' || (data.nomineeDocType2!'')?lower_case == 'demat account id' || (data.nomineeDocType3!'')?lower_case == 'demat account id'>
        <#if (data.appointNominee!'') != 'no'><img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" /></#if>
    </#if>
</div> Demat Account ID

</td>
    <td style="border: 1px solid black; padding: 5px; width: 20%;">
        <#if (data.appointNominee!'') != 'no'>${(data.nomineeDocNo1!'')?upper_case}</#if>
    </td>

    <td style="border: 1px solid black; padding: 5px; width: 20%;">
        <#if (data.appointNominee!'') != 'no'>${(data.nomineeDocNo2!'')?upper_case}</#if>
    </td>

    <td style="border: 1px solid black; padding: 5px; width: 20%;">
        <#if (data.appointNominee!'') != 'no'>${(data.nomineeDocNo3!'')?upper_case}</#if>
    </td>
    </tr>
    <tr>
        <td style="border: 1px solid black; padding: 5px; width: 40%;"><strong>10. Relationship of Guardian with nominee </strong> </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;"><strong> </strong></td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;"> </td>
        <td style="border: 1px solid black; padding: 5px; width: 20%;"> </td>
    </tr>



</table>

<table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:3px; border:none;">
    <!-- Header Row -->
    <tr>
        <td style="border: 1px solid black; padding: 5px; width: 70%;" colspan="2"> </td>
        <td style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">Name(s) of holder(s)</td>
        <td style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">Signature(s) of holder*</td>
    </tr>

    <!-- Sole / First Holder -->
    <tr>
        <td style="border: 1px solid black; padding: 5px; font-weight: bold;" colspan="2">Sole / First Holder (Mr./Ms.)</td>
        <td style="border: 1px solid black; padding: 5px;">
            <#if (data.appointNominee!'') != 'no'>
            <strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong>
            </#if>
        </td>
        <td style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;"> </td>
    </tr>

    <!-- Second Holder -->
    <tr>
        <td style="border: 1px solid black; padding: 5px; font-weight: bold;" colspan="2">Second Holder (Mr./Ms.)</td>
        <td style="border: 1px solid black; padding: 5px;"> </td>
        <td style="border: 1px solid black; padding: 5px; text-align: center;"> </td>
    </tr>

    <!-- Third Holder -->
    <tr>
        <td style="border: 1px solid black; padding: 5px; font-weight: bold;" colspan="2">Third Holder (Mr./Ms.)</td>
        <td style="border: 1px solid black; padding: 5px;"> </td>
        <td style="border: 1px solid black; padding: 5px; text-align: center;"> </td>
    </tr>

    <!-- Signature Disclaimer -->
    <tr style="border:none;">
        <td colspan="4" style=" padding: 5px; font-size: 10px; border:none;">
            * Signature of witness, along with name and address are required, if the account holder affixes thumb impression, instead of signature.
        </td>
    </tr>
    <tr style="border:none;">
        <td colspan="4" style=" padding: 5px; font-size: 15px; font-weight: bold; text-align:center; border:none;">
            OR
        </td>
    </tr>
</table>


<table style="border: 1px solid black;">

    <tr style="border:none;">
        <td colspan="4" style=" padding: 5px; font-size: 15px; font-weight: bold; text-align:center; border:none;">
            Annexure - B
        </td>
    </tr>
    <tr style="border:none;">
        <td colspan="4" style=" padding: 5px; font-size: 15px; font-weight: bold; text-align:center; border:none;">
            Declaration Form for opting out of Nomination
        </td>
    </tr>
    <tr style="border:none;">
        <td colspan="4" style=" padding: 5px; font-size: 11px;  text-align:left; font-style: italic; border:none;">
          <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;">
            <#if (data.appointNominee!'') == 'no'>
            <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
            </#if>
            </div>
          I/We wish to opt out of a nomination. [Declaration Form opting out of nomination as prescribed by SEBI]
        </td>
    </tr>

    <tr style="border:none;">

        <td colspan="2" style="border:none;"> </td>
        <td colspan="2" style=" padding: 5px; font-size: 11px;  text-align:right; border:none;">

          <table style="width: 100%; border-collapse: collapse; border:none;">
                <#assign dateStr = data.currentDateDmY!''>
                <tr style="border:none;">
                    <strong style="position:relative; top:7px;  font-size: 14px; ">  Date </strong>
                    <#list 0..<8 as i>
                    <td style="border: 1px solid black; text-align: center;"><#if (i < dateStr?length)>${dateStr[i..i]}</#if></td>
                    </#list>
                </tr>
            </table>
        </td>

    </tr>


     <tr>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">UCC / TRADING CODE </td>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; "> </td>
    </tr>
      <tr>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">DP ID </td>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">
             <table style="width: 100%; border-collapse: collapse; border:none;">

                <tr style="border:none;">

                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">I</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">N</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">3</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">0</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">3</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">1</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">1</td>
                    <td style=" text-align: center; height: 10px; font-weight: bold; border:none;">6</td>
                </tr>
            </table>
         </td>
    </tr>
    <tr>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">Client ID (only for Demat account) </td>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; "> </td>
    </tr>
     <tr>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">Sole/First Holder Name </td>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">
            <#if (data.appointNominee!'') == 'no'>
            <strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong>
            </#if>

         </td>
    </tr>
     <tr>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">Second Holder Name
</td>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; "> </td>
    </tr>
     <tr style="border: 1px solid black;">
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; ">Third Holder Name </td>
         <td colspan="2" style="border: 1px solid black; padding: 5px; font-size: 11px; "> </td>
    </tr>
</table>
	 <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 14 -------- </span>
<table>
     <tr style="border: 1px solid black;">
        <td colspan="4" style=" padding: 5px; font-size: 11px;  text-align:justify; ">
          I/ We hereby confirm that I / We do not wish to appoint any nominee(s) in my / our MF Folio / trading / demat account and understand the issues involved in
non-appointment of nominee(s) and further are aware that in case of death of all the account holder(s), my / our legal heirs would need to submit all the requisite
documents / information for claiming of assets held in my / our MF Folio / trading / demat account, which may also include documents issued by Court or other
such competent authority, based on the value of assets held in the MF Folio / trading / demat account
        </td>
    </tr>

   </table>

   <br />

<table style="border:none;">

     <tr style=" margin-top:20px; border:none; ">
        <td colspan="6" style=" padding: 5px; font-size: 15px; font-weight: bold; text-align:center; border:none;">
            Name and Signature of Holder(s)*
        </td>
    </tr>

    <tr style="border:none;">
        <td colspan="2" style=" padding: 5px; font-weight: bold; border:none; "><span style="text-align: center;">1. <u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u></span></td>
        <td colspan="2" style=" padding: 5px; font-weight: bold; border:none;"><span style="text-align: center;">2.______________________</span></td>
        <td colspan="2" style=" padding: 5px; font-weight: bold; border:none;"><span style="text-align: center;">3._____________________</span></td>
    </tr>
<br />
<br />
    <tr style="border:none;">
        <td colspan="2" style=" padding: 5px; font-weight: bold; border:none;"><span style="text-align: center;">______________________</span></td>
        <td colspan="2" style=" padding: 5px; font-weight: bold; border:none;"><span style="text-align: center;">_______________________</span></td>
        <td colspan="2" style=" padding: 5px; font-weight: bold; border:none;"><span style="text-align: center;">______________________</span></td>
    </tr>



</table>

 <table style="border:none; margin-top:20px;">
    <tr style="border:none;">
        <td colspan="6" style=" padding: 5px; font-size: 10px; border:none;">
            * Signature of witness, along with name and address are required, if the account holder affixes thumb impression, instead of signature.
        </td>
    </tr>
 </table>
	 <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 15 -------- </span>




  <div class="WordSectionneww100">
        <div style="text-align: left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>
        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>



<table style="width: 100%; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 11px; border: 1px solid black;">

    <tr>
        <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">Depository Participant ID IN303116</td>
  </tr>
    <tr style="background-color: #f2f2f2;">
        <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">Charges for Depository Services</td>
    </tr>
    <tr>
        <td colspan="4" style="border: 1px solid black; padding: 5px; font-weight: bold; text-align: center;">The Investor will have to choose one of the schemes to pay the charges to VSL for the services offered.</td>
    </tr>
        <tr>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;" colspan="1">PARTICULARS Please Select any one scheme </td>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">SCHEME - A  <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div></td>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">SCHEME - B  <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div></td>
            <td style="border: 1px solid black; padding: 5px; font-weight: bold;">SCHEME - C  <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div></td>
        </tr>
         <tr>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;"> </td>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">With POA </td>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Without POA </td>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">With POA</td>
        </tr>

        <tr>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">A) Documentation Charges </td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil </td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
        </tr>
         <tr>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">B) Refundable Deposit (Non Interest bearing) </td>
          <td style="border: 1px solid black; padding: 5px; "> Nil</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs. 3,000/-</td>
        </tr>
         <tr>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">C) Account Maintenance</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px;">Nil </td>
          <td style="border: 1px solid black; padding: 5px;">Nil</td>
        </tr>
        <tr>
          <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Payable at the time of opening the Account</td>
          <td style="border: 1px solid black; padding: 5px; "> Nil</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs. 3,000/-</td>
        </tr>
         <tr>
         <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Custody Chargest</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px;">Nil </td>
          <td style="border: 1px solid black; padding: 5px;">Nil</td>
        </tr>
          <tr>
         <td style="border: 1px solid black; padding: 5px; ">Dematerialisation Charges (Per Request)</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs. 200/-</td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 200/- </td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 200/-</td>
        </tr>
         <tr>
         <td style="border: 1px solid black; padding: 5px;">Rematerialisation Charges (Per Request)</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs. 50/-</td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 50/- </td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
        </tr>

         <tr>
         <td style="border: 1px solid black; padding: 5px; font-weight: bold;">Transaction Charges</td>
          <td style="border: 1px solid black; padding: 5px; "> </td>
          <td style="border: 1px solid black; padding: 5px;"> </td>
          <td style="border: 1px solid black; padding: 5px;"> </td>
        </tr>
        <tr>
         <td style="border: 1px solid black; padding: 5px; ">With VSL (Buy)</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px;">Nil </td>
          <td style="border: 1px solid black; padding: 5px;">Nil</td>
        </tr>

        <tr>
          <td style="border: 1px solid black; padding: 5px;">Within VSL (Sell) (Market &amp; Off Market)</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs. 20/-</td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 50/- </td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 12/-</td>
        </tr>
        <tr>
         <td style="border: 1px solid black; padding: 5px; ">Outside VSL (Buy)</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px;">Nil </td>
          <td style="border: 1px solid black; padding: 5px;">Nil</td>
        </tr>

         <tr>
             <td style="border: 1px solid black; padding: 5px; ">Outside VSL (Sell) (Market &amp; Off Market)</td>
         <td style="border: 1px solid black; padding: 5px; ">Rs.50/- or 0.05%
of value whichever is
higher)</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs.50/- or 0.05%
of value whichever is
higher</td>
          <td style="border: 1px solid black; padding: 5px;">Rs.50/- or 0.05%
of value whichever is
higher </td>

        </tr>


         <tr>
         <td style="border: 1px solid black; padding: 5px; ">Pledge creation</td>
          <td style="border: 1px solid black; padding: 5px; ">Rs. 50/-</td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 50/- </td>
          <td style="border: 1px solid black; padding: 5px;">Rs. 50/-</td>
        </tr>

         <tr>
         <td style="border: 1px solid black; padding: 5px; ">Pledge closure</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px;">Nil </td>
          <td style="border: 1px solid black; padding: 5px;">Nil</td>
        </tr>
         <tr>
         <td style="border: 1px solid black; padding: 5px; ">Pledge invocation</td>
          <td style="border: 1px solid black; padding: 5px; ">Nil</td>
          <td style="border: 1px solid black; padding: 5px;">Nil </td>
          <td style="border: 1px solid black; padding: 5px;">Nil</td>
        </tr>
         <tr>
         <td style="border: 1px solid black; padding: 5px; ">.</td>
          <td style="border: 1px solid black; padding: 5px; "> </td>
          <td style="border: 1px solid black; padding: 5px;"> </td>
          <td style="border: 1px solid black; padding: 5px;"> </td>
        </tr>
</table>



        <div class="page-break"> </div>


    <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px; border:none;">
        <tr style="border:none;">
            <th colspan="2" style="padding: 5px; text-align: left; border:none;">Notes :-</th>

        </tr>
      <br />

        <tr style="border:none;">
            <td colspan="2" style="padding: 5px; margin-top:10px; text-align: justify; border:none;"> 1) Cheque returned charges will be levied in actual as charged by the banks.
<br />2) Interest @ 13% p.a. shall be charged, if the bill is not paid by due date.
<br />3) In case of Corporate Demat Account: AMC of Rs. 500/- p.a. will be levied by NSDL in addition to VSL charges.
<br />4) The above rates are based on the existing NSDL charges and may change from time to time.
<br />5) The scheme once selected can be changed only at the end of financial year.
<br />6) Refundable deposit will be repaid only on closure of account. No adjustment will be made in the interim.
<br />7) Any extra statement would be charged @ Rs. 25/- per statement for one page and thereafter, it would be charged @
<br />Rs. 2/- per page.
<br />8) Actual charges levied by NSDL for CAS statement will be charged seperately.
<br />9) Statutory levies as applicable would be charged extra.</td>
        </tr>
    </table>


    <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-top:10px; border:none;">
        <tr>
            <td style="padding: 5px; text-align: center; font-weight: bold; border: 1px solid black;">Declaration for Basic Service Demat Account (BSDA)</td>

        </tr>


        <tr style="border:none;">
          <td style=" padding: 5px;  border:none;">Please select any one option given below,</td>
        </tr>
        <tr style="border:none;">
              <td style=" padding: 5px; border:none;">1.
                 <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;">
                    <img src="data:image/png;base64,${data.checkmarkBase64!''}" style="height:10px;width:10px;" />
              </div>  I/We wish to open Regular Demat Account.
            </td>

        </tr>

    <tr style="border:none;">
         <td style=" padding: 5px; border:none;">2. <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div>  I/We wish to open BSDA Account. </td>

    </tr>
    <tr style="border:none;">
        <td style=" padding: 5px; border:none;">3. <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div>  I/We wish to Opt out of BSDA.</td>

    </tr>
</table>



 <table style="border-collapse: collapse; margin: auto; width:100%; border:none; margin-top:50px">
                <tr style="width:100%; border:none;">
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center;  line-height:1;">
                            <span>_______________________________</span><br /><br />
                             <b>   Signature of the 1st Holder</b>
                         </p>
                    </td>
                    <td style="border:none;">
                       <p style="font-size: 11px; text-align: center; line-height:1;">
                            <span>_______________________________</span><br /><br />
                              <b>  Signature of the 2nd Holder</b>
                       </p>
                    </td>
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center;  line-height:1;">
                            <span>_______________________________</span><br /><br />
                                <b>Signature of the 3rd Holder</b>
                         </p>

                    </td>
                </tr>
</table>



   <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 16 -------- </span>

        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align: left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>
        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>



    <table style="border:none;">
         <tr style="border:none;">
           <td colspan="4" style=" padding: 5px; font-size: 11px; text-align: center; font-weight: bold; border:none;">OPTION FOR ISSUANCE OF DIS BOOKLET ALONGWITH ACCOUNT OPENING </td>
         </tr>
          <tr style="border:none;">
           <td colspan="4" style=" padding: 5px; font-size: 11px; font-style:italic; border:none;">(to be filled by persons seeking to open a depository account who have given Power of Attorney / DDPI to operate the depository
account to a stock broker/Participant/Portfolio Manager and do not intend to open a Basic Services Demat Account) </td>
         </tr>
         <tr style="border:none;">
           <td colspan="2" style=" padding: 5px; font-size: 11px; font-weight: bold; border:none;">Ventura Securities Limited,</td>
           <td colspan="2" style=" padding: 5px; font-size: 11px; border:none;">

               <table style="width: 100%; border-collapse: collapse;">

                <tr>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">DP ID</td>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">I</td>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">N</td>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">3</td>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">0</td>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">3</td>
                    <td style=" text-align: center; border: 1px solid black; height: 10px; font-weight: bold;">1</td>
                    <td style=" text-align: center;  border: 1px solid black; height: 10px; font-weight: bold;">1</td>
                    <td style=" text-align: center;  border: 1px solid black; height: 10px; font-weight: bold;">6</td>
                </tr>
            </table>


           </td>
         </tr>


         <tr>
           <td colspan="2" style="padding: 5px; font-size: 11px;">
                I-THINK TECHNO CAMPUS,<br />
                "B" WING, 8TH FLOOR,<br />
                OFF. POKHRAN ROAD NO. 2,<br />
                CLOSE TO EASTERN EXPRESS HIGHWAY, THANE (WEST) - 400607,<br />
                MAHARASHTRA, INDIA.<br />
                TEL.: 6754 7000 / 6622 7100
            </td>
           <td colspan="2" style=" padding: 5px; font-size: 11px; "> </td>
         </tr>


     </table>

<table style="width: 100%; border-collapse: collapse; font-family: sans-serif; font-size: 11px;">
    <tr>
        <td rowspan="3" style="width: 25%; padding: 8px; vertical-align: top; border: 1px solid #000;">
            Name(s)<br />of account<br />holder(s)
        </td>
        <td style="width: 75%; padding: 8px; border: 1px solid #000;">
            Sole/ First holder :  <strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong>
        </td>
    </tr>
    <tr style="background-color: #f5f5f5;">
        <td style="padding: 8px; border: 1px solid #000;">
            Second holder
        </td>
    </tr>
    <tr style="background-color: #f5f5f5;">
        <td style="padding: 8px; border: 1px solid #000;">
            Third holder
        </td>
    </tr>
</table>



<table style="width: 100%; border-collapse: collapse; font-family: sans-serif; font-size: 11px; margin-top:10px;">

    <tr>
        <td style="padding: 5px; border: 1px solid #000; font-weight: bold;">
            Option for Issue of DIS booklet <span style="font-style:italic;">(please tick any one)</span>
        </td>
    </tr>
    <tr>
        <td style="padding: 5px; border: 1px solid #000;">
          <b> Option 1 </b> <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"> </div><br />
I/We wish to receive the Delivery Instruction Slip (DIS) booklet with account opening.
        </td>
    </tr>

    <tr>
        <td style="padding: 5px; border: 1px solid #000;">
          <b> Option 2 </b> <div class="square-box" style="width: 12px; height: 12px; margin-top:5px; display: inline-block; text-align: center; line-height: 12px; text-transform: uppercase; position:relative; top:3px;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></div><br />
I/We do not wish to receive the DIS booklet with account opening. However, the DIS booklet should be issued to me/ us within
reasonable time on my/ our request at any later date.
        </td>
    </tr>


</table>



<table style="width: 100%; border-collapse: collapse; font-family: sans-serif; font-size: 11px; text-align: left;  margin-top:10px;" border="1">
    <thead>
        <tr>
            <th style="width: 35%; padding: 8px; border: 1px solid #000; font-weight: bold;"> </th>
            <th style="width: 35%; padding: 8px; border: 1px solid #000; font-weight: bold;">Name(s) of holder(s)</th>
            <th style="width: 30%; padding: 8px; border: 1px solid #000; font-weight: bold;">Signature(s) of holder</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Sole / First Holder (Mr./Ms.)</td>
            <td style="padding: 8px; border: 1px solid #000;"><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></td>
            <td style="padding: 8px; border: 1px solid #000;"> </td>
        </tr>
        <tr>
            <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Second Holder (Mr./Ms.)</td>
            <td style="padding: 8px; border: 1px solid #000;"> </td>
            <td style="padding: 8px; border: 1px solid #000;"> </td>
        </tr>
        <tr>
            <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Third Holder (Mr./Ms.)</td>
            <td style="padding: 8px; border: 1px solid #000;"> </td>
            <td style="padding: 8px; border: 1px solid #000;"> </td>
        </tr>
    </tbody>
</table>

<br /><br />

<table style="width: 100%; font-family: sans-serif; font-size: 11px; border:none;">
    <tr style="border:none;">
        <td style="padding: 8px; font-weight: bold; border:none;">Place: ${(data.userCity!'')?upper_case}</td>

    </tr>
    <tr style="border:none;">
        <td style="padding: 8px; font-weight: bold; border:none;">Date <u><strong> ${data.currentDate!''} </strong></u></td>
    </tr>
</table>


      <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 17 -------- </span>

        <div class="page-break"> </div>
        <br />
  <div class="WordSectionneww100">
        <div style="text-align: left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
              <div style="float: right;  display: inline-block; border: 2px solid black; padding: 5px 12px; font-weight: bold; font-size: 16px; font-family: sans-serif;">
    MANDATORY
</div>
        </div>

        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>




         <div style="font-family: sans-serif; font-size: 11px; line-height: 1.5;">
    <h3 style="text-align: center; font-size: 16px;">ACKNOWLEDGEMENT</h3>

    <p>Date :- <u><strong> ${data.currentDate!''} </strong></u></p>

    <p>
        Ventura Securities Limited,<br />
        I-Think Techno Campus,<br />
        "B" Wing, 8th Floor, Off. Pokhran Road No. 2,<br />
        Close to Eastern Express Highway,<br />
        Thane (West) - 400607, Maharashtra, India.
    </p>

    <p>Dear Sir,</p>

    <p>
        This has reference to necessary Know Your Client Form containing basic information, additional information and other documents executed by me. I hereby acknowledge receipt of following documents from you:-
    </p>

    <ol>
        <li>
            Documents containing mandatory Rights &amp; Obligations of stock broker, authorised person and client for trading on BSE/NSE/MCX/NCDEX exchanges (including additional rights &amp; obligations in case of internet / wireless technology based trading).
        </li>
        <li>
            Risk Disclosure Documents.
            <ol type="a">
                <li>For BSE &amp; NSE</li>
                <li>For MCX &amp; NCDEX</li>
            </ol>
        </li>
        <li>
            Document detailing do's and don'ts for trading on exchange, for the education of the investors.
            <ol type="a">
                <li>For BSE &amp; NSE</li>
                <li>For MCX &amp; NCDEX</li>
            </ol>
        </li>
        <li>Policy and Procedures document.</li>
        <li>Additional Voluntary clauses forming part and parcel of mandatory rights and obligations.</li>
        <li>Rights &amp; Obligations of beneficial owner and depository participant</li>
    </ol>

    <p>
        I state that I have read and understood all above documents and these documents are binding upon me.
    </p>

    <p>Thanking you,</p>


    <table style="width: 100%; font-size: 11px; border:none;">
        <tr style="border:none;">
            <td style="width: 50%; padding: 5px; border:none;"><strong>Signature of the Applicant</strong> : __________________________</td>
        </tr>
        <tr style="border:none;">
            <td style="width: 50%; padding: 5px; border:none;"><strong>Name of the Applicant</strong> : <u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u></td>
        </tr>
    </table>
</div>



    <div style="font-family: sans-serif; font-size: 11px; ">

    <table style="width: 100%; border-collapse: collapse; margin-bottom: 10px; border:none;">
        <tr style="border:none;">
            <td style="width: 10%; font-weight: bold; border:none;">DP ID</td>
            <td style="width: 10%; border: 1px solid black; text-align: center; font-weight: bold; border:none;">IN303116</td>
            <td style="text-align: center; font-weight: bold; border:none;" colspan="2">ACKNOWLEDGEMENT</td>
        </tr>
    </table>

    <p style="font-size: 11px; font-weight: normal;">
        <strong>Regd. and correspondence Office:</strong>
        I-Think Techno Campus, "B" Wing, 8th Floor, Off. Pokhran Road No. 2,
        Close to Eastern Express Highway, Thane (West) - 400607, Maharashtra, India.
        Tel.: 6754 7000 / 6622 7100 &nbsp;&nbsp;
        <strong>Email ID:</strong> dpcare@ventura1.com
    </p>



    <p style="font-weight: normal;">Received the application from Mr/Ms <u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u></p>
    <p style="font-weight: normal;">as the sole/first holder alongwith _______________________________ and _______________________________</p>
    <p style="font-weight: normal;">as the second and third holders respectively for opening of a depository account.
    Please quote the DP Id &amp; Client Id allotted to you in all your future correspondence.</p>

    <br />

    <table style="width: 100%; font-weight: normal; border:none;">
        <tr style="border:none;">
            <td style="width: 50%; border:none;">Date: <u> ${data.currentDate!''} </u></td>
            <td style="text-align: right; border:none;">
                <strong>For Ventura Securities Ltd.</strong><br />
                Authorised Signatory
            </td>
        </tr>
    </table>


</div>

          <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 18 -------- </span>

        <div class="page-break"> </div>

        <br />
  <div class="WordSectionneww100">
        <div style="text-align:left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
              <div style="float: right;  display: inline-block; border: 2px solid black; padding: 5px 12px; font-weight: bold; font-size: 16px; font-family: sans-serif;">
    VOLUNTARY
</div>
        </div>

        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>



<div style="font-size: 11px; font-weight: normal; ">

    <p><strong>Date: <u> ${data.currentDate!''} </u></strong></p>

    <p style=" font-weight: normal;">
        <strong>Ventura Securities Limited</strong><br />
        I-Think Techno Campus,<br />
        "B" Wing, 8th Floor, Off. Pokhran Road No. 2,<br />
        Close to Eastern Express Highway,<br />
        Thane (West) - 400607, Maharashtra, India.
    </p>

    <p style="padding:0;"><strong>Dear Sir,</strong></p>

    <p style="text-decoration: underline; font-weight: bold; text-align:center;">
        Sub.: Letter of Authority / Request to Ventura Securities Limited
    </p>

    <p style=" font-weight: normal;">
        I, the undersigned, have opened the client / constituent account with Ventura Securities Limited (herein referred to as "VENTURA")
        for trading / dealing in securities on the Bombay Stock Exchange Limited (BSE) and / or National Stock Exchange of India Limited (NSE)
        and/or Multi Commodity Exchange of India Limited (MCX) and/or National Commodity and Derivatives Exchange Limited (NCDEX). In
        respect of my dealing / account with you, I hereby request / instruct and authorize VENTURA to do the followings:-
    </p>

    <ol>
        <li>To accept verbal instructions for placement / modification / cancellation of orders.</li>
        <li>
            To maintain a running account instead of settlement of my trade related dues and/or delivery of securities or commodities on a
            bill-to-bill / settlement-to-settlement basis.
        </li>
        <li>
            To retain the securities received by VENTURA on my behalf from Exchange(s) on pay-out or otherwise against my debit balance /
            trade related dues/ exposure/ trading limits/ open interest/ various margins or as per any regulations of BSE and/or NSE and/or MCX
            and/or NCDEX. Such retaining / holding of securities shall be construed as due compliance of the requirement of exchange(s) and SEBI.
            Further, VENTURA has the sole discretion and authority (i) to use / Transfer the above securities to the clearing corporation/
            clearing member/ exchange(s) for the purpose of early pay-in / margin, Additional Base Capital; (ii) to dispose/sell the above
            securities to meet any monetary / other trade related dues/obligation(s) not fulfilled by me towards VENTURA/ the exchange(s).
        </li>
        <li>
            To hold payout of funds / credit in my account and pay to me only to the extent demanded by me. To use/ adjust the credit balance
            available from time to time in my account(s) for my exposure/ trading limits/ margin requirements / other dues.
        </li>
        <li>
            However, I prefer to settle the account on following basis (Please tick in Appropriate Box):<br />

            <table style="margin-top:10px;">
                <tr>
                <td style="padding: 8px; border: 1px solid #000;">Quarterly</td>
                <td style="padding: 8px; border: 1px solid #000;"><img src="data:image/png;base64,${data.checkmarkBase64!''}" alt="Checkmark" style="height: 10px; width: 10px;" /></td>
                <td style="padding: 8px; border: 1px solid #000;">Monthly</td>
                <td style="padding: 8px; border: 1px solid #000;"> </td>
                </tr>
            </table>
        </li>
        <li>
            To debit/credit/ transfer of amounts, either on same Exchange and / or between various segments of the same exchange and / or
            between the exchanges across various segment and / or depository / demat charges to meet my debit balance or various dues
            payable to VENTURA / Exchanges in relation to my trades.
        </li>
        <li>
            To transfer credit / debit balance from mark to mark and/or premium account to margin account and vice versa.
        </li>
        <li>
            All the instructions / requests placed by me on telephone while being using login and Password shall be always binding upon me.
        </li>
        <li>
            I hereby agree to receive telephone calls / SMS on my mobile number / email ID as registered by me with VENTURA, the
            messages / communications relating to transactions, PIN, passwords, stock ideas, real time news pertaining to market, updates
            on stock prices and any other messages (including products and services) as sent by VENTURA from time to time.
        </li>
    </ol>

    <p>
        Further, I reserve my right to withdraw the above instructions at any time. In such event, I undertake to inform you in writing and
        such communication shall be addressed to the above address.
    </p>


    <p><strong> _____________________________ </strong><br />
    <em>Signature of the Applicant</em></p>

    <p><strong>Name of the Applicant:  <u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u></strong></p>

</div>




      <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 19 -------- </span>



        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align:right;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>

        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>


 <div id="" style="font-family: sans-serif; font-size: 11px; line-height: 1.6;">

  <div style="text-align: right; font-weight: bold; font-size: 14px;">Appendix A</div>

    <div style="text-align: center; font-weight: bold; font-size: 16px; margin-bottom: 20px;">
        ELECTRONIC CONTRACT NOTE (ECN)11 - DECLARATION
    </div>

    <p>
        To,<br />
        Ventura Securities Ltd.,<br />
        I-ThinkTechno Campus, "B" Wing, 8th Floor,<br />
        Pokhran Road No. 2, Off. Eastern Express Highway,<br />
        Thane (West) - 400 607. Maharashtra, India.
    </p>

    <p>Dear Sir,</p>

    <p style="font-weight: normal;">
        I, <u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u>, a client with<br /><br />
        member M/s. <strong>VENTURA SECURITIES LIMITED of BSE/NSE/MCX/NCDEX</strong> Exchange undertake as follows:
    </p>

    <ul style="margin-top: 0;">
        <li>
            I am aware that the Member has to provide physical contract note in respect of all the trades placed by me unless I myself want the same in the electronic form. I am voluntarily requesting for delivery of electronic contract note pertaining to all the trades carried out / ordered by me.
        </li>
        <li>
    My email ID is * <u><strong>${(data.email!'')?upper_case}</strong></u> and/or secondary email ID
    <span style="border-bottom: 1px solid #000; display: inline-block; width: 250px;">&nbsp;</span>
</li>
        <li>
            I am aware that non-receipt of bounced mail notification by the member shall amount to delivery of the contract note at the above email id.
        </li>
        <li>
            This authorization has been signed by the me only and not by any authorised person on my behalf or holder of the Power of Attorney.
        </li>
        <li>
            Please note that any change in my email-id shall be communicated by me through a physical letter to Ventura Securities Ltd. In respect of internet clients, the request for change of email id shall be made through the secured access by way of client specific user id and password provided to me.
        </li>
    </ul>

    <br /><br />

    <p>
        <strong>Signature of the Applicant</strong> : <span style="border-bottom: 1px solid #000; display: inline-block; width: 100px;"> </span>
    </p>

    <p>
        <strong>Date</strong> : <u><strong>${data.currentDate!''}</strong></u><br /><br />
        <strong>Place</strong> : ${(data.userCity!'')?upper_case}    </p>

</div>

              <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 20 -------- </span>


        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align:left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>

        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>


        <div style="text-align: right; width:100%; font-size:16px;">
            <strong style="font-size:16px;">FIRST HOLDER</strong>
        </div>

  <table style="font-size: 11px; margin-top:10px; width:100%; border:none; ">
        <tr>
            <td colspan="5" style="font-weight: bold; width:60%; font-size:15px; border:none;">SELF-CERTIFICATION FOR INDIVIDUAL</td>
            <td colspan="2" style="padding: 5px; border: 1px solid #000; font-weight: bold; width:10%;">Client Code:</td>
            <td colspan="3" style="padding: 5px; border: 1px solid #000; font-weight: bold; width:30%;"> </td>

        </tr>
        <tr>
            <td colspan="10" style="font-weight: bold; text-align:center; font-size:15px; border:none;">FATCA/CRS DECLARATION FORM</td>
        </tr>
        <tr>
            <td colspan="10" style="font-weight: bold; text-align:left; border: 1px solid #000;">Part I- Please fill in the country for each of the following:</td>
        </tr>

         <tr>
            <td style=" text-align:left; border: 1px solid #000;">1.</td>
            <td colspan="9" style=" text-align:left; border: 1px solid #000;">Country Of:</td>
        </tr>
        <tr>
            <td colspan="1" style="font-weight: bold; border: 1px solid #000; width: 10%;"> </td>
            <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%; ">a)</td>
            <td colspan="3" style="padding: 5px; border: 1px solid #000; width: 30%;">Birth</td>
            <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;"><strong>${(data.fatcaCountryBirthName!'')?upper_case}</strong></td>
        </tr>
         <tr>
            <td colspan="1" style="font-weight: bold; border: 1px solid #000; width: 10%;"> </td>
            <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%; ">b)</td>
            <td colspan="3" style="padding: 5px; border: 1px solid #000; width: 30%;">Citizenship</td>
            <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;"><strong>${(data.fatcaCitizenshipName!'')?upper_case}</strong></td>
        </tr>
         <tr>
            <td colspan="1" style="font-weight: bold; border: 1px solid #000; width: 10%;"> </td>
            <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%; ">b)</td>
            <td colspan="3" style="padding: 5px; border: 1px solid #000; width: 30%;">Residence for Tax Purposes</td>
            <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;"><strong>${(data.fatcaTaxResidenceName!'')?upper_case}</strong></td>
        </tr>
        <tr>
            <td colspan="1" style="padding: 5px; border: 1px solid #000; width: 10%; ">2.</td>
            <td colspan="4" style="padding: 5px; border: 1px solid #000; width: 40%;">US Person (Yes / No)</td>
            <td colspan="5" style="padding: 5px; border: 1px solid #000; width: 50%;"><strong>NO</strong></td>
        </tr>


        <tr>
            <td colspan="10" style="font-weight: bold; text-align:left; border: 1px solid #000;">Part II- Please note:</td>
        </tr>
        <tr>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;"> </td>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;">a.</td>
            <td colspan="8" style=" text-align:left; border: 1px solid #000; width: 80%;">If in all fields above, the country mentioned by you is India and if you do not have US person status, please proceed to part
             III for signature.</td>
        </tr>
        <tr>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;"> </td>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;">b.</td>
            <td colspan="8" style=" text-align:left; border: 1px solid #000; width: 80%;">If for any of the above field, the country mentioned by you is not India and/or if your US person status is Yes, please provide
             the Tax Payer Identification Number (TIN) or functional equivalent as issued in the specific country in the table below:</td>
        </tr>
        <tr>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;">i)</td>
            <td colspan="4" style=" text-align:left; border: 1px solid #000; width: 40%;">TIN &nbsp;&nbsp;&nbsp;&nbsp; &nbsp; ${(data.taxIdentificationNumber!'')?upper_case}</td>
            <td colspan="2" style=" text-align:left; border: 1px solid #000; width: 20%;">Country of Issue</td>
            <td colspan="3" style=" text-align:left; border: 1px solid #000; width: 30%;"><strong>${(data.fatcaTaxResidenceName!'')?upper_case}</strong></td>
        </tr>
         <tr>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;">ii)</td>
            <td colspan="4" style=" text-align:left; border: 1px solid #000; width: 40%;">TIN</td>
            <td colspan="2" style=" text-align:left; border: 1px solid #000; width: 20%;">Country of Issue</td>
            <td colspan="3" style=" text-align:left; border: 1px solid #000; width: 30%;"> </td>
        </tr>
          <tr>
            <td colspan="1" style=" text-align:left; border: 1px solid #000; width: 10%;">iii)</td>
            <td colspan="4" style=" text-align:left; border: 1px solid #000; width: 40%;">TIN</td>
            <td colspan="2" style=" text-align:left; border: 1px solid #000; width: 20%;">Country of Issue</td>
            <td colspan="3" style=" text-align:left; border: 1px solid #000; width: 30%;"> </td>
        </tr>




  </table>
  <table style="width:100%; border-collapse: collapse; font-size: 11px; border: 1px solid #000; margin-top:10px " border="1" cellpadding="5">
    <tr style="border:none;">
        <td style="padding: 5px; border:none;" colspan="2">
            <strong>a.</strong> In case any of the parameters in <strong>Part I</strong> indicates that you are a US person or a person resident outside of India for tax purpose and you do not have Taxpayer Identification Numbers/functional equivalent, please complete and sign the Self-Certification section given in <strong>Part IV</strong>.
        </td>
    </tr>
    <tr style="border:none;">
        <td style="padding: 5px; border:none;" colspan="2">
            <strong>b.</strong> In case you are declaring US person status as 'No' but your Country of Birth is US, please provide document evidencing Relinquishment of Citizenship. If not available provide reasons for not having relinquishment certificate.
        </td>
    </tr>
    <tr style="border:none;">
        <td style="padding: 5px; border:none;" colspan="2">
            <strong>c.</strong> Please also fill <strong>Part IV Self-Certification</strong>.
        </td>
    </tr>
    <tr style="border:none;">
        <td style="padding: 5px; border:none;" colspan="2">
            <strong>Part III- Customer Declaration (Applicable for all customers)</strong><br /><br />
            <strong>(i)</strong> Under penalty of perjury, I/we certify that:
            <ol type="1" style="padding-left: 20px; margin-top: 5px;">
                <li style="margin-bottom: 5px;">
                    The applicant is (i) an applicant taxable as a US person under the laws of the United States of America ("U.S.") or any state or political subdivision thereof or therein, including the District of Columbia or any other states of the U.S., (ii) an estate the income of which is subject to U.S. federal income tax regardless of the source thereof. <strong>(This clause is applicable only if the account holder is identified as a US person)</strong>
                </li>
                <li>
                    The applicant is an applicant taxable as a tax resident under the laws of country outside India. <strong>(This clause is applicable only if the account holder is a tax resident outside of India)</strong>
                </li>
            </ol>
        </td>
    </tr>
    <tr style="border:none;">
        <td style="padding: 5px;  border-bottom:1px solid #000; border-top:none;" colspan="2">
            <strong>(ii)</strong> I/We understand that the Ventura is relying on this information for the purpose of determining the status of the applicant named above in compliance with FATCA/CRS. The Ventura is not able to offer any tax advice on CRS or FATCA or its impact on the applicant. I/We shall seek advice from professional tax advisor for any tax questions.
        </td>
    </tr>
    <tr style="border:none; ">
        <td style="padding: 5px; border-top:1px solid #000; border-bottom:none;" colspan="2">
            <strong>(iii)</strong> I/We agree to submit a new form within 30 days if any information or certification on this form becomes incorrect.
        </td>
    </tr>
	   <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 21 -------- </span>
    <tr style="border:none; ">
        <td style="padding: 5px; border:none;" colspan="2">
            <strong>(iv)</strong> I/We agree that as may be required by domestic regulators/tax authorities the Ventura may be required to report, reportable details to CBDT or close or suspend my account.
        </td>
    </tr>
    <tr style="border:none;">
        <td style="padding: 5px; border:none;" colspan="2">
            <strong>(v)</strong> I/We certify that I/We provide the information on this form and to the best of my/our knowledge and belief the certification is true, correct, and complete including the taxpayer identification number of the applicant.
        </td>
    </tr>
</table>

<table style="width:100%; border-collapse: collapse;  font-size: 11px;" border="1" cellpadding="5">
    <tr>
        <td style="width: 50%; padding: 8px; border: 1px solid #000;"><strong>Signature :</strong></td>
        <td style="width: 50%; padding: 8px; border: 1px solid #000;"> </td>
    </tr>
    <tr>
        <td style="padding: 8px; border: 1px solid #000;"><strong>Name : ${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></td>
        <td style="padding: 8px; border: 1px solid #000;"><strong>Date (DD/MM/YYYY) : ${data.currentDate!''}</strong></td>

    </tr>
    <tr>
        <td colspan="2" style="padding: 8px; border: 1px solid #000; font-size:16px;"><strong>Part IV- Self Certification:</strong></td>
    </tr>
    <tr>
        <td colspan="2" style="padding: 8px; border: 1px solid #000;">
            <div>To be filled only if-<br />
                (a) Name of the country in Part I is other than India TIN or functional equivalent is not available, or<br />
                (b) US person is mentioned as YES in Part I, and TIN is not available
            </div>
        </td>
    </tr>
    <tr>
        <td style="padding: 8px; border: 1px solid #000;">
            I confirm that I am neither a US person nor a resident for tax purpose in any country other than India, though one or more parameters suggest my relation with the country outside India. Therefore, I am providing the following document as proof of my citizenship and residency in India.
        </td>
         <td style="padding: 8px; border: 1px solid #000;"> </td>
    </tr>
    <tr>
        <td colspan="2" style="padding: 8px; border: 1px solid #000;">
            <strong>Document Proof submitted (Please tick document being submitted)</strong><br /><br />
            <label><input type="checkbox" style="vertical-align: middle;" checked="checked" /> <span style="margin-top:5px;">Passport </span></label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" /> Election Id</label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" checked="checked" /> PAN Card</label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" /> Driving License</label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" /> Aadhar Card</label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" /> NREGA</label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" /> Job Card</label>&nbsp;&nbsp;
            <label style="vertical-align: middle;"><input type="checkbox" /> Govt. Issued Id Card</label>
        </td>
    </tr>
</table>

<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 22 -------- </span>



        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align:left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>

        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>

        <div style="font-family: Arial, sans-serif; font-size: 14px; width: 100%; line-height: 1.5;">

    <div style="text-align: right; font-weight: bold;">Annexure A</div>

    <div style="text-align: center; font-weight: bold; font-size: 16px; margin: 10px 0;">
        Most Important Terms and Conditions (MITC)<br />
        <span style="font-size: 14px;">(For non-custodial settled trading accounts)</span>
    </div>

    <ol style="padding-left: 20px;">
        <li>Your trading account has a "Unique Client Code" (UCC) ________________________________, different from your demat account number. Do not allow anyone (including your own stock broker, their representatives and dealers) to trade in your trading account on their own without taking specific instruction from you for your trades. Do not share your internet/ mobile trading login credentials with anyone else.</li>

        <li>You are required to place collaterals as margins with the stock broker before you trade. The collateral can either be in the form of funds transfer into specified stock broker bank accounts or margin pledge of securities from your demat account. The bank accounts are listed on the stock broker website. Please do not transfer funds into any other account. The stock broker is not permitted to accept any cash from you.</li>

        <li>The stock broker's Risk Management Policy provides details about how the trading limits will be given to you, and the tariff sheet provides the charges that the stock broker will levy on you.</li>

        <li>All securities purchased by you will be transferred to your demat account within one working day of the payout. In case of securities purchased but not fully paid by you, the transfer of the same may be subject to limited period pledge i.e. seven trading days after the pay-out (CUSPA pledge) created in favor of the stock broker. You can view your demat account balances directly at the website of the Depositories after creating a login.</li>

        <li>The stock broker is obligated to deposit all funds received from you with any of the Clearing Corporations duly allocated in your name. The stock broker is further mandated to return excess funds as per applicable norms to you at the time of quarterly/monthly settlement. You can view the amounts allocated to you directly at the website of the Clearing Corporation(s).</li>

        <li>You will get a contract note from the stock broker within 24 hours of the trade.</li>

        <li>You may give a one-time Demat Debit and Pledge Instruction (DDPI) authority to your stock broker for limited access to your demat account, including transferring securities, which are sold in your account for pay-in.</li>

        <li>The stock broker is expected to know your financial status and monitor your accounts accordingly. Do share all financial information (e.g. income, net worth, etc.) with the stock broker as and when requested for. Kindly also keep your email ID and mobile phone details with the stock broker always updated.</li>

        <li>In case of disputes with the stock broker, you can raise a grievance on the dedicated investor grievance ID of the stock broker. You can also approach the stock exchanges and/or SEBI directly.</li>

        <li>Any assured/guaranteed/fixed returns schemes or any other schemes of similar nature are prohibited by law. You will not have any protection/recourse from SEBI/stock exchanges for participation in such schemes.</li>
    </ol>

    <br />


<table style="width: 100%; border-collapse: collapse; font-family: sans-serif; font-size: 11px; text-align: left;  margin-top:10px;" border="1">
    <thead>
        <tr>
            <th style="width: 35%; padding: 8px; border: 1px solid #000; font-weight: bold;"> </th>
            <th style="width: 35%; padding: 8px; border: 1px solid #000; font-weight: bold;">Name(s) of holder(s)</th>
            <th style="width: 30%; padding: 8px; border: 1px solid #000; font-weight: bold;">Signature(s) of holder</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="padding: 8px; border: 1px solid #000; font-weight: bold;">Sole / First Holder (Mr./Ms.)</td>
            <td style="padding: 8px; border: 1px solid #000;"><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></td>
            <td style="padding: 8px; border: 1px solid #000;"> </td>
        </tr>
    </tbody>
</table>
	  <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 23 -------- </span>


        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align:right;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
        </div>

        <div>
            <p style="font-size: 1px; text-align: center;"> * </p>
        </div>




        <div style="font-family: Arial, sans-serif; font-size: 13px; width: 100%; line-height: 1.5;">

    <h4 style="margin-bottom: 5px;">Notes :</h4>

    <ol style="padding-left: 20px;">
        <li> <strong>All communications shall be sent at the address of the Sole/First holder only. </strong></li>
        <li>
            <strong>Thumb impression and signatures other than English or Hindi or any of the other languages not contained in the 8th Schedule of the Constitution of India must be attested by a Magistrate or a Notary Public or a Special Executive Magistrate.</strong>
        </li>
        <li>
            <strong>Instructions related to nomination, are as below:</strong>
            <ol type="I" style="padding-left: 20px;">
                <li>The Nomination can be made only by individuals holding beneficiary owner accounts on their own behalf singly or jointly.</li>
                <li>Non-individuals including society, trust, body corporate, partnership firm, karta of Hindu Undivided Family, holder of power of attorney cannot nominate. If the account is held jointly all joint holders will sign the nomination form.</li>
                <li>A minor can be nominated. In that event, the name and address of the Guardian of the minor nominee shall be provided by the beneficial owner.</li>
                <li>The nominee(s) shall not be a trust, society, body corporate, partnership firm, karta of Hindu Undivided Family or a power of attorney holder. A non-resident Indian can be a nominee, subject to the exchange controls in force, from time to time.</li>
                <li>Nomination in respect of the beneficiary owner account stands rescinded upon closure of the beneficiary owner account. Similarly, the nomination in respect of the securities shall stand terminated upon transfer of the securities.</li>
                <li>Transfer of securities in favour of a Nominee(s) shall be valid discharge by the depository and the Participant against the legal heir.</li>
                <li>
                    The cancellation of nomination can be made by individuals only holding beneficiary owner accounts on their own behalf singly or jointly by the same persons who made the original nomination. Non-individuals including society, trust, body corporate, partnership firm, karta of Hindu Undivided Family, holder of power of attorney cannot cancel nomination. If the beneficiary owner account is held jointly, all joint holders will sign the cancellation form.
                </li>
                <li>On cancellation of the nomination, the nomination shall stand rescinded and the depository shall not be under any obligation to transfer the securities in favour of the Nominee(s).</li>
                <li>Nomination can be made upto three nominees in a demat account. In case of multiple nominees, the Client must specify the percentage of share for each nominee that shall total upto hundred percent. In the event of the beneficiary owner not indicating any percentage of allocation/share for each of the nominees, the default option shall be to settle the claims equally amongst all the nominees.</li>
                <li>On request of Substitution of existing nominees by the beneficial owner, the earlier nomination shall stand rescinded. Hence, details of nominees as mentioned in the FORM at the time of substitution will be considered. Therefore, please mention the complete details of all the nominees.</li>
                <li>Copy of any proof of identity must be accompanied by original for verification or duly attested by any entity authorized for attesting the documents, as provided in Annexure D.</li>
                <li>Savings bank account details shall only be considered if the account is maintained with the same participant.</li>
                <li>DP ID and Client ID shall be provided where demat details is required to be provided.</li>
            </ol>
        </li>
        <li>
            <strong>For receiving Statement of Account in electronic form:</strong>
            <ol type="i" style="padding-left: 20px;">
                <li>Client must ensure the confidentiality of the password of the email account.</li>
                <li>Client must promptly inform the Participant if the email address has changed.</li>
                <li>Client may opt to terminate this facility by giving 10 days prior notice. Similarly, Participant may also terminate this facility by giving 10 days prior notice.</li>
            </ol>
        </li>
        <li>Strike off whichever is not applicable.</li>
        <li>In case physical share certificate are to be converted to demat, the same has to be handed over to the branch ONLY alongwith duly filled DRF form.</li>
    </ol>

    <br />
  <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 24 -------- </span>

    <table style="border:none;">
      <thead>
        <tr>
            <th style=" padding: 8px;  font-weight: bold; border:none;">For Office Use :</th>

        </tr>
    </thead>
    <tbody>
        <tr>
           <td style=" padding: 5px; border:none;"> I verify that the Account opening form is in order.</td>
        </tr>
        <tr>
           <td style=" padding: 5px; border: 1px solid #000;"> Name :</td>
        </tr>
        <tr>
           <td style=" padding: 5px; border: 1px solid #000;"> Signature :</td>
        </tr>
        <tr>
           <td style=" padding: 5px; border: 1px solid #000;"> Date :</td>
        </tr>
    </tbody>
    </table>

 <table style="margin-top:10px; width:100%;">
      <thead>
        <tr>
            <th style=" padding: 8px; border: 1px solid #000;  font-weight: bold;"> </th>
            <th style=" padding: 8px; border: 1px solid #000;  font-weight: bold;">Date</th>
            <th style=" padding: 8px; border: 1px solid #000;  font-weight: bold;">Name</th>
            <th style=" padding: 8px; border: 1px solid #000;  font-weight: bold;">Signature</th>


        </tr>
    </thead>
    <tbody>

        <tr>
           <td style=" padding: 5px; border: 1px solid #000; width:26%;"> Checked &amp; Entered in DPM</td>
           <td style=" padding: 5px; border: 1px solid #000; width:10%;"> </td>
           <td style=" padding: 5px; border: 1px solid #000; width:27%;"> </td>
           <td style=" padding: 5px; border: 1px solid #000; width:27%;"> </td>
        </tr>
        <tr>
           <td style=" padding: 5px; border: 1px solid #000; width:26%;">Released in DPM</td>
           <td style=" padding: 5px; border: 1px solid #000; width:10%;"> </td>
           <td style=" padding: 5px; border: 1px solid #000; width:27%;"> </td>
           <td style=" padding: 5px; border: 1px solid #000; width:27%;"> </td>
        </tr>

    </tbody>
    </table>

</div>

  <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 25 -------- </span>


        <div class="page-break"> </div>

 <div style="height: 300px;">
    <!-- Your page content goes here -->
  </div>

  <!-- Ventura-style Footer -->
  <div style="position: relative; width: 100%;">
    <div style="background-color: white; text-align: center; padding: 40px 20px 20px 20px; font-family: Arial, sans-serif; font-size: 13px;">
      <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 70px; vertical-align: middle;" /><br /><br />
      I-Think Techno Campus, "B" Wing, 8<sup>th</sup> Floor, Off. Pokhran Road No. 2,<br />
      Close to Eastern Express Highway, Thane (West) - 400607, Maharashtra, India.<br />
      <strong>Tel.</strong>: 91-22-6754 7000 / 6622 7100<br />
      <strong>Website</strong>: <a href="https://www.venturasecurities.com" target="_blank" style="color: black; text-decoration: none;">www.venturasecurities.com</a>
    </div>
    <div style="background-color: #f26b2b; height: 150px;"> </div>
  </div>
<span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 26 -------- </span>

        <div class="page-break"> </div>


     <!-- Logo -->
  <div style="padding: 20px;">
    <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 100px;" />
  </div>

  <!-- Title -->
  <div style="text-align: left; font-weight: bold; font-size: 16px; padding-bottom: 10px;">
    Declaration Cum Undertaking Given By Client To DP
  </div>

  <!-- Address Block -->
  <div style="padding: 0 20px; font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5;">
    To,<br />
    <strong>Ventura Securities Limited (DP ID - IN303116)</strong><br />
    "B" Wing, 8th Floor,<br />
    I-Think Techno Campus,<br />
    Pokhran Road, No.2,<br />
    Off Eastern Express Highway,<br />
    Thane - 400 607, Maharashtra, India.
  </div>

  <!-- Subject -->
  <div style="padding: 20px; font-weight: bold; font-size: 14px;">
    Sub : NRI DP Account Opening
  </div>

  <!-- Letter Content -->
  <div style="padding: 0 20px; font-family: Arial, sans-serif; font-size: 14px; line-height: 1.6;">
    Dear Sir<br /><br />
    I/We hereby declare that I/We have read and understood the present<br />
    Rules and Regulations of the FEMA pertaining to investment in securities market.<br /><br />

    In addition to above, I/We also confirm that I/We have complied and will<br />
    continue to comply with FEMA Rules and Regulations.<br /><br />

    Thanking you.<br /><br />
    Yours faithfully,
  </div>

  <!-- Signature Fields -->
  <div style="border: 1px solid black; margin: 20px; padding: 15px; font-family: Arial, sans-serif; font-size: 14px;">
    <div style="margin-bottom: 15px;">
      <strong>Name :</strong> &nbsp;&nbsp;&nbsp;<u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ____________ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ____________
    </div>
    <div>
      <strong>Signature :-</strong><br />
     <table style="border-collapse: collapse; margin: auto; width:100%; border:none;">
                <tr style="width:180%; border:none;">
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center; ">
                            <span>_________________________</span><br /><br />
                             <b>   Signature of the 1st Holder</b>
                         </p>
                    </td>
                    <td style="border:none;">
                       <p style="font-size: 11px; text-align: center; ">
                            <span>_________________________</span><br /><br />
                              <b>  Signature of the 2nd Holder</b>
                       </p>
                    </td>
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center;  ">
                            <span>_________________________</span><br /><br />
                                <b>Signature of the 3rd Holder</b>
                         </p>

                    </td>
                </tr>
            </table>
    </div>
  </div>

  <!-- Place and Date -->
  <div style="padding: 0 20px; font-family: Arial, sans-serif; font-size: 14px;">
    Place : ${(data.userCity!'')?upper_case}<br /><br />
    Date &nbsp;&nbsp;&nbsp; : <u><strong>${data.currentDate!''}</strong></u>
  </div>


      <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 27 -------- </span>

        <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align:right;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
              <div style="float: left;  display: inline-block; border: 2px solid black; padding: 5px 12px; font-weight: bold; font-size: 16px; font-family: sans-serif;">
    MANDATORY
</div>
        </div>



    <div style="padding: 20px; font-family: Arial, sans-serif; font-size: 11px; line-height: 1.6; color: #000;">
    <h2 style="text-align: center; font-weight: bold; margin-bottom: 20px; font-size: 18px;">
      POLICY OF VENTURA SECURITIES LIMITED (VSL) TO HANDLE GOOD TILL CANCEL (GTC) OR GOOD TILL TRIGGER (GTT) ORDER(S) PLACED BY CLIENTS
    </h2>

    <p style="font-weight: normal;">
      Certain front-end terminals of VSL has facility to place orders which shall remain in the system till a particular period or till it is cancelled or till it is triggered and may get converted into trades. The actual rule of the Exchanges requires Stock Brokers to lay down a policy to handle such orders and communicate the same to their clients. The policy of VSL to handle such order is as under and it aims to appraise the clients about the functionality, validity and execution of such order:
    </p>

    <h3 style="margin-top: 20px; font-size:16px;">GTT Orders functionality</h3>

    <p style="font-weight: normal;">
      GTT (Good Till Triggered) orders are branded <strong>"For365"</strong> at VSL. If the order remains open, it will be automatically canceled after 365 days from the date of order placement, or expiry of the contract, whichever is earlier.
    </p>

    <p style="font-weight: normal;">
      <strong>For365</strong> order allows customers to place a buy or sell order that remains in our system in passive mode, until a specified trigger price is reached. Once the trigger price is reached, the order is placed on the exchange trading platform, subject to RMS checks such as margin required or stock availability in holdings as the case may be.
    </p>

    <p style="font-weight: normal;">
      For365 orders help investors capture favourable market movements by avoiding a burden of constant monitoring.
    </p>

    <p style="font-weight: normal;">
      For365 orders can be a single order or an OCO (One Cancels the Other) order. Under single orders, the investor is required to provide a direction (either buy or sell), a trigger price and a limit price. If the trigger price is reached, then the order is placed with the exchange trading system in the limit price.
    </p>

    <p style="font-weight: normal;">
      Under OCO orders, the investor is required to provide a direction (buy or sell) and trigger price and limit prices for both legs - stop loss and target. If the trigger price for one leg is hit, the order for that leg is placed with the exchange trading platform, and the other leg is automatically cancelled.
    </p>

    <h3 style="margin-top: 20px; font-size:16px;">Risk on Corporate Actions</h3>

    <p style="font-weight: normal;">
      When a corporate action happens, such as a stock split or a bonus or a merger or a significant dividend announcement, etc., it can alter the stock's price as well as its derivatives price substantially. If a <strong>For365</strong> order is in place for that stock, the trigger price may become absurd, due to the new price levels created by the corporate action. This could result in the order getting placed on the exchange and getting executed at an unintended price, resulting in a loss to the client.
    </p>

    <p style="font-weight: normal;">
      Corporate actions that impact the price could be dividends, splits, bonus, mergers, acquisition, rights issues, spin-off, carve outs, demerger, capital reduction, buy-backs, OFS etc. There doesn't appear to be a corporate action that will not impact price.
    </p>

    <h3 style="margin-top: 20px; font-size:16px;">Handling of pending orders in the event of corporate announcement</h3>

    <ol style="padding-left: 20px;">
      <li>
        All <strong>For365</strong> pending orders placed by customers in the <strong>stocks or F&amp;O contract</strong> where corporate action is declared, would be canceled except for <strong>"Dividends"</strong> as corporate action. It is the considered opinion of VSL that the Dividend announcement may not impact the price of the stock substantially and canceling the order of the client due to such announcement may not be in the interest of the client.
      </li>
      <li>
        All pending orders cancellation communication would be sent via email to respective customers <strong>@4 PM</strong>, 2 days prior to ex-date.
      </li>
      <li>
        This impending order cancellation information would also be displayed on App and Web on dashboard as notification.
      </li>
      <li>
        The orders would be cancelled <strong>@11:30 PM one day prior to ex-date</strong> and fresh orders placement in that stock or F&amp;O contract will not be allowed past 11:30 PM.
      </li>
      <li>
        Fresh order placement will be reactivated <strong>@9:15 AM on ex-date</strong>
      </li>
    </ol>
   <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 28 -------- </span>
    <p style="font-weight: normal;">
      <strong>For Example</strong>: If Stock "x" declared bonus and ex-date is 18th August 2024. Those customers having For365 pending orders in that stock would receive a communication about order cancellation @4 PM on 16th August 2024 (including Sundays / Or Holidays). All pending orders would be cancelled @11:30 PM on 17th August 2024 and no fresh orders would be allowed post 11:30 PM. Customers can place fresh For365 orders in that stock or F&amp;O contract from 9:15 AM on 18th August.
    </p>

    <h3 style="margin-top: 20px; font-size:16px;">Types Of Corporate Actions</h3>

    <ol style="padding-left: 20px;">
      <li>
        <strong>Dividends</strong>: Dividends are payments a firm provides to its shareholders, typically in the form of a profit distribution that heavily relies on the investor's return on investment.
      </li>
      <li>
        <strong>Stock Splits</strong>: A stock split is a corporate decision that lowers the price per share while raising the total number of shares in circulation in a corporation.
      </li>
      <li>
        <strong>Mergers and Acquisitions</strong>: When two businesses that are comparatively the same size combine to establish a new, larger firm, this is known as a merger. Acquisitions take place when one business purchases another.
      </li>
      <li>
        <strong>Rights Issues</strong>: An offering in which a business grants current shareholders the option to buy additional shares at a reduced price is known as a rights issue.
      </li>
      <li>
        <strong>Bonus Issues</strong>: Bonus issue is a sort of offering in which a corporation gives away free additional shares to its current owners. According to a predefined ratio, shareholders receive more shares based on the shares they already possess.
      </li>
      <li>
        <strong>Tender Offers</strong>: A tender offer is a sort of offering in which a business proposes to pay a higher price to its shareholders in exchange for a predetermined number of shares.
      </li>
      <li>
        <strong>Buybacks</strong>:  A corporation may choose to repurchase its shares on the open market as part of a buyback program, sometimes referred to as a share repurchase plan.
      </li>
      <li>
        <strong>Spinoffs</strong>: A spinoff is a type of corporate activity when a firm separates a subsidiary or business unit from the parent company to form a new, independent company. Shares of the new firm, which later becomes a distinct, publicly listed corporation, are often distributed to parent company shareholders.
      </li>
      <li>
        <strong>Carve-outs</strong>: A carve-out is a corporate action when a firm sells a portion of a subsidiary or business unit to the general public or a private investor while keeping a stake in the company
      </li>
      <li>
        <strong>Capital reduction</strong>: Capital reductions are generally done through share cancellations, paid-back capital, or share repurchases (buybacks).
      </li>
      <li>
        <strong> Tender offer</strong>: A tender offer is a sort of offering in which a business proposes to pay a higher price to its shareholders in exchange for a predetermined number of shares.
      </li>

    </ol>
     <p style="font-weight: normal;">
      I acknowledge receipt of above policy of Good till cancel (GTT) OR Good Till Trigger (GTT) have been read and understood by me.
    </p>
    <br />


    <p style="font-size:14px;">
        <strong>Signature of the Applicant</strong> : <span style="border-bottom: 1px solid #000; display: inline-block; width: 200px;">&nbsp;</span><br /><br />
        <strong>Name of the Applicant</strong> : <u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u>
    </p>

  </div>

     <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 29 -------- </span>


      <div class="page-break"> </div>

  <div class="WordSectionneww100">
        <div style="text-align:left;">
            <img src="data:image/jpeg;base64,${data.venturaLogoBase64!''}" alt="Ventura Logo" style="height: 61px; width: 150px;" />
              <div style="float: right;  display: inline-block; border: 2px solid black; padding: 5px 12px; font-weight: bold; font-size: 16px; font-family: sans-serif;">
    VOLUNTARY
</div>
        </div>


      <div style="font-family: Arial, sans-serif; font-size: 14px; padding: 20px; color: #000; line-height: 1.5;">
    <div style="text-align: left;">Date : <u><strong>${data.currentDate!''}</strong></u></div>

    <div style="font-weight: bold;">Ventura Securities Limited,</div>
    <div>No. 1, Nana Street, 2<sup>nd</sup> Floor, T Nagar,<br />Chennai - 600017, TamilNadu, India.</div>

    <p style=" font-weight: normal; font-size:11px;">
      </p><p style="font-weight: bold;">Dear Sir, </p>
      I / We are hereby executing following Demat Debit and Pledge instruction (DDPI) in favour of Ventura Securities Limited, authorizing them to operate aforesaid beneficiary account DP ID IN303116,<br />
      Client Id ____________________________ of Client Code _________________________ for the below mentioned specific purposes.
    <p></p>

    <table border="1" cellspacing="0" cellpadding="6" style="width: 100%; border-collapse: collapse; text-align: left; margin-top: 20px; font-size:11px;">
      <tr style="font-weight: bold; background-color: #f0f0f0;">
        <td style="width: 5%;  padding: 5px; border: 1px solid #000;">Sr.</td>
        <td style="width: 60%; padding: 5px; border: 1px solid #000;">Purpose</td>
        <td style="width: 11%; padding: 5px; border: 1px solid #000;">Signature of First / Sole Holder</td>
        <td style="width: 12%; padding: 5px; border: 1px solid #000;">Signature of Second Holder</td>
        <td style="width: 12%; padding: 5px; border: 1px solid #000;">Signature of Third Holder</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">1</td>
        <td style=" padding: 5px; border: 1px solid #000;">Transfer of securities held in the beneficial owner accounts of the client towards Stock Exchange related deliveries / Settlement obligations arising out of trades executed by clients on the Stock Exchange through the same stockbroker.</td>
        <td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">2</td>
        <td style=" padding: 5px; border: 1px solid #000;">Pledging / re-pledging of securities in favour of trading member (TM) / clearing member (CM) for the purpose of meeting margin requirements of the clients in connection with the trades executed by the clients on the Stock Exchange.</td>
       <td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">3</td>
        <td style=" padding: 5px; border: 1px solid #000;">Mutual Fund transactions being executed on Stock Exchange order entry platforms.</td>
       <td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">4</td>
        <td style=" padding: 5px; border: 1px solid #000;">Tendering shares in open offers through Stock Exchange platforms.</td>
       <td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td><td style=" padding: 5px; border: 1px solid #000;"> </td>
      </tr>
    </table>

    <p style="font-size: 11px;"><strong>Note:</strong> This authorization will continue to remain valid until revoked in writing by you (pursuant to SEBI/HO/MIRSD/MIRSD-PoD-1/P/CIR/2022/137 dated October 06, 2022)</p>

    <p style="font-size:11px;">
      I / we understand that the above instruction shall result into movement / pledge / re-pledge of securities to the demat account of Ventura Securities Limited as mentioned here below:
    </p>



    <table border="1" cellspacing="0" cellpadding="6" style="width: 100%; border-collapse: collapse; text-align: left; font-size:11px;">
      <tr style="font-weight: bold; background-color: #f0f0f0;">
        <td style=" padding: 5px; border: 1px solid #000;">Sr.</td>
        <td style=" padding: 5px; border: 1px solid #000;">DP ID</td>
        <td style=" padding: 5px; border: 1px solid #000;">Client ID</td>
        <td style=" padding: 5px; border: 1px solid #000;">DP Name</td>
        <td style=" padding: 5px; border: 1px solid #000;">Client Name</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">1.</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN303116</td>
        <td style=" padding: 5px; border: 1px solid #000;">10003942 - IN563542</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">2.</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN303116</td>
        <td style=" padding: 5px; border: 1px solid #000;">10003959 - IN564498</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">3.</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN303116</td>
        <td style=" padding: 5px; border: 1px solid #000;">12958507 - IN528838</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">4.</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN303116</td>
        <td style=" padding: 5px; border: 1px solid #000;">13121388</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">5.</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN303116</td>
        <td style=" padding: 5px; border: 1px solid #000;">13121396</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
        <td style=" padding: 5px; border: 1px solid #000;">Ventura Securities Limited</td>
      </tr>
      <tr>
        <td style=" padding: 5px; border: 1px solid #000;">6.</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN001150</td>
        <td style=" padding: 5px; border: 1px solid #000;">IN620031 - Mutual Fund</td>
        <td style=" padding: 5px; border: 1px solid #000;">Indian Clearing Corporation Limited</td>
        <td style=" padding: 5px; border: 1px solid #000;">Indian Clearing Corporation Limited</td>
      </tr>
    </table>

    <div style="margin-top: 30px;">
      <table style="border-collapse: collapse; margin: auto; width:100%; border:none;">
                <tr style="width:180%; border:none;">
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center; ">
                            <span><u><strong>${(data.firstName!'')?upper_case} ${(data.middleName!'')?upper_case} ${(data.lastName!'')?upper_case}</strong></u></span><br /><br />
                             <b>  1. Name of First / Sole Holder</b>
                         </p>
                    </td>
                    <td style="border:none;">
                       <p style="font-size: 11px; text-align: center; ">
                            <span>_________________________</span><br /><br />
                              <b> 2. Name of Second Holder</b>
                       </p>
                    </td>
                    <td style="border:none;">
                         <p style="font-size: 11px; text-align: center;  ">
                            <span>_________________________</span><br /><br />
                                <b>3. Name of Third Holder</b>
                         </p>

                    </td>
                </tr>
            </table>
    </div>
  </div>
    <span style="position:absolute; bottom:0; left:40%;margin-bottom: 20px;font-size: 10px;"> -------- 30 -------- </span>

</div>

</body>
</html>

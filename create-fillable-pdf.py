"""
One-time script to create a fillable AcroForm PDF from the original KYC template.

Adds named text fields at specific positions on each page so that
KycPdfStampService can fill them by name using PDFBox PDAcroForm API:
   form.getField("firstName").setValue("Samanta")

Run: python create-fillable-pdf.py

Input:  Facilon-platform-API/src/main/resources/pdf/kyc-form-template.pdf
Output: Facilon-platform-API/src/main/resources/pdf/kyc-form-fillable.pdf
"""

import sys
import os

# Use the correct Python with pypdf
from pypdf import PdfReader, PdfWriter
from pypdf.generic import (
    NameObject, TextStringObject, NumberObject, ArrayObject,
    DictionaryObject, IndirectObject
)
from pypdf.constants import AnnotationDictionaryAttributes as ADA

INPUT_PDF = os.path.join(os.path.dirname(__file__),
    "Facilon-platform-API", "src", "main", "resources", "pdf", "kyc-form-template.pdf")
OUTPUT_PDF = os.path.join(os.path.dirname(__file__),
    "Facilon-platform-API", "src", "main", "resources", "pdf", "kyc-form-fillable.pdf")

# Field definitions: (field_name, page_1indexed, x, y, width, height)
# x, y = bottom-left corner in PDF coordinates (origin at page bottom-left)
# Positions estimated from pdfplumber analysis + visual inspection

FIELDS = [
    # === PAGE 5 — KYC Personal Details (image-based page) ===
    # These positions are approximate for the scanned form image
    ("namePrefix",        5,  155, 703, 60,  12),
    ("applicantName",     5,  220, 703, 320, 12),
    ("firstName",         5,  155, 685, 150, 12),
    ("middleName",        5,  370, 685, 150, 12),
    ("lastName",          5,  155, 667, 320, 12),
    ("fatherSpouseName",  5,  155, 649, 370, 12),
    ("motherName",        5,  155, 631, 370, 12),
    ("dateOfBirth",       5,  155, 613, 100, 12),
    ("gender",            5,  340, 613, 100, 12),
    ("maritalStatus",     5,  155, 595, 150, 12),
    ("citizenship",       5,  155, 577, 150, 12),
    ("residentialStatus", 5,  340, 577, 200, 12),
    ("occupationType",    5,  155, 559, 370, 12),
    ("panNumber",         5,  480, 703, 100, 12),
    ("aadhaarNumber",     5,  480, 577, 100, 12),

    # Tax residence
    ("taxCountryCode",    5,  100, 490, 100, 12),
    ("taxIdNumber",       5,  250, 490, 200, 12),
    ("taxIdType",         5,  470, 490, 100, 12),

    # Proof of Identity
    ("idType",            5,  155, 440, 200, 12),
    ("idNumber",          5,  155, 422, 200, 12),
    ("idExpiryDate",      5,  450, 422, 100, 12),

    # === PAGE 6 — Address + Contact (image-based page) ===
    ("permAddress1",      6,  155, 720, 400, 11),
    ("permAddress2",      6,  155, 705, 400, 11),
    ("permAddress3",      6,  155, 690, 400, 11),
    ("permCity",          6,  155, 670, 180, 11),
    ("permState",         6,  370, 670, 180, 11),
    ("permCountry",       6,  155, 653, 180, 11),
    ("permPinCode",       6,  370, 653, 100, 11),

    # Correspondence address
    ("corrAddress1",      6,  155, 600, 400, 11),
    ("corrCity",          6,  155, 580, 180, 11),
    ("corrState",         6,  370, 580, 180, 11),
    ("corrCountry",       6,  155, 563, 180, 11),
    ("corrPinCode",       6,  370, 563, 100, 11),

    # Contact
    ("mobile",            6,  155, 510, 200, 12),
    ("email",             6,  155, 492, 300, 11),

    # IPV
    ("ipvDoneBy",         6,  215, 182, 100, 11),
    ("ipvDate",           6,  420, 182, 100, 11),

    # === PAGE 13 — Trading Details (text-based page) ===
    ("incomeRange",       13, 280, 698, 100, 11),
    ("netWorth",          13, 280, 698, 100, 11),
    ("tradingOccupation", 13, 175, 668, 200, 11),

    # Bank details
    ("bankName",          13, 175, 560, 200, 11),
    ("bankBranch",        13, 175, 544, 200, 10),
    ("bankCity",          13, 175, 527, 200, 11),
    ("bankAccountNo",     13, 175, 511, 200, 11),
    ("bankAccountType",   13, 175, 494, 200, 11),
    ("micrNo",            13, 175, 478, 200, 11),
    ("ifscCode",          13, 175, 461, 200, 11),

    # Depository
    ("dpName",            13, 175, 420, 200, 11),
    ("depository",        13, 240, 404, 80,  11),
    ("dpId",              13, 175, 372, 150, 11),

    # === PAGE 14 — Trading Experience + Email ===
    ("investmentExperience", 14, 175, 760, 100, 11),
    ("ecnEmail",          14, 175, 503, 250, 10),
    ("ecnSecondaryEmail", 14, 175, 489, 250, 10),

    # === PAGE 15 — Demat Account ===
    ("dematPan",          15, 400, 647, 120, 11),

    # === PAGE 16 — Nomination ===
    ("nominee1Name",      16, 135, 580, 120, 10),
    ("nominee1Relation",  16, 135, 560, 120, 10),
    ("nominee1Dob",       16, 135, 540, 100, 10),
    ("nominee1Share",     16, 135, 520, 50,  10),
    ("nominee2Name",      16, 300, 580, 120, 10),
    ("nominee3Name",      16, 450, 580, 120, 10),

    # === PAGE 22 — Acknowledgement ===
    ("ackApplicantName",  18, 200, 300, 250, 11),

    # === PAGE 28 — FATCA ===
    ("fatcaClientCode",   24, 200, 700, 200, 11),
    ("fatcaCountryBirth", 24, 200, 640, 200, 11),
    ("fatcaCitizenship",  24, 200, 620, 200, 11),
    ("fatcaTaxResidence", 24, 200, 600, 200, 11),
    ("fatcaUsPerson",     24, 200, 580, 100, 11),
    ("fatcaTin",          24, 200, 540, 200, 11),
]


def create_text_field(writer, page_obj, field_name, x, y, w, h, page_index):
    """Create a text form field annotation on the given page."""

    # Create the field dictionary
    field = DictionaryObject()
    field.update({
        NameObject("/Type"): NameObject("/Annot"),
        NameObject("/Subtype"): NameObject("/Widget"),
        NameObject("/FT"): NameObject("/Tx"),  # Text field
        NameObject("/T"): TextStringObject(field_name),
        NameObject("/V"): TextStringObject(""),
        NameObject("/Rect"): ArrayObject([
            NumberObject(x),
            NumberObject(y),
            NumberObject(x + w),
            NumberObject(y + h),
        ]),
        NameObject("/F"): NumberObject(4),  # Print flag
        NameObject("/Ff"): NumberObject(0),  # No flags (editable)
        NameObject("/DA"): TextStringObject(f"/Helv {min(h-2, 10)} Tf 0 0 0.4 rg"),  # Dark blue text
        NameObject("/Q"): NumberObject(0),   # Left-aligned
        NameObject("/MK"): DictionaryObject({
            NameObject("/BC"): ArrayObject([]),  # No border color (invisible border)
        }),
        NameObject("/Border"): ArrayObject([
            NumberObject(0), NumberObject(0), NumberObject(0),  # No visible border
        ]),
    })

    return field


def main():
    print(f"Reading: {INPUT_PDF}")
    reader = PdfReader(INPUT_PDF)
    writer = PdfWriter()

    # Clone all pages
    writer.clone_reader_incoming_fields = False
    for page in reader.pages:
        writer.add_page(page)

    print(f"Total pages: {len(writer.pages)}")

    # Create AcroForm
    # We need to set up the form with default resources
    acroform = DictionaryObject()
    acroform.update({
        NameObject("/NeedAppearances"): NameObject("/true"),
        NameObject("/DR"): DictionaryObject({
            NameObject("/Font"): DictionaryObject({
                NameObject("/Helv"): DictionaryObject({
                    NameObject("/Type"): NameObject("/Font"),
                    NameObject("/Subtype"): NameObject("/Type1"),
                    NameObject("/BaseFont"): NameObject("/Helvetica"),
                }),
            }),
        }),
    })

    fields_array = ArrayObject()
    field_count = 0

    for field_name, page_num, x, y, w, h in FIELDS:
        page_index = page_num - 1
        if page_index >= len(writer.pages):
            print(f"  SKIP {field_name}: page {page_num} > {len(writer.pages)}")
            continue

        page_obj = writer.pages[page_index]

        field = create_text_field(writer, page_obj, field_name, x, y, w, h, page_index)

        # Add field ref to the indirect objects
        field_ref = writer._add_object(field)

        # Link field to page
        field[NameObject("/P")] = page_obj.indirect_reference

        # Add to page annotations
        if "/Annots" not in page_obj:
            page_obj[NameObject("/Annots")] = ArrayObject()
        page_obj[NameObject("/Annots")].append(field_ref)

        # Add to form fields array
        fields_array.append(field_ref)
        field_count += 1

    acroform[NameObject("/Fields")] = fields_array
    writer._root_object[NameObject("/AcroForm")] = acroform

    print(f"Added {field_count} form fields")

    # Save
    with open(OUTPUT_PDF, "wb") as f:
        writer.write(f)

    size_mb = os.path.getsize(OUTPUT_PDF) / 1024 / 1024
    print(f"Saved: {OUTPUT_PDF} ({size_mb:.1f} MB)")

    # Verify
    verify_reader = PdfReader(OUTPUT_PDF)
    verify_fields = verify_reader.get_fields()
    if verify_fields:
        print(f"Verification: {len(verify_fields)} AcroForm fields found")
        for name in list(verify_fields.keys())[:10]:
            print(f"  {name}")
        print(f"  ... and {len(verify_fields) - 10} more" if len(verify_fields) > 10 else "")
    else:
        print("WARNING: No AcroForm fields found in output!")


if __name__ == "__main__":
    main()

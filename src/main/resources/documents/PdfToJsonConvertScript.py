import pdfplumber
import re
import json

text = ""
with pdfplumber.open(
        "Downloads\\-Azərbaycan Respublikasının İnzibati Xətalar Məcəlləsi.pdf") as pdf:
    for page in pdf.pages:
        text += page.extract_text() + "\n"

text = re.sub(r"M\s*a\s*d\s*d\s*ə", "Maddə", text)

text = re.sub(r'\[\d+\]', '', text)

text = re.sub(r'KM\d+', '', text)

text = re.sub(r"\s+", " ", text)
text = re.sub(r"Maddə\s*(\d+)\s*\.", r"\n\nMaddə \1.", text)

laws = {}

main_pattern = r"Maddə\s+(\d+(?:-\d+)?)\."
main_matches = list(re.finditer(main_pattern, text))

for i, match in enumerate(main_matches):
    article_number = match.group(1)
    start = match.end()
    end = main_matches[i + 1].start() if i + 1 < len(main_matches) else len(text)
    article_text = text[start:end].strip()

    escaped_num = re.escape(article_number)

    first_sub_match = re.search(rf'{escaped_num}\.\d+\.', article_text)

    if first_sub_match:
        article_title = article_text[:first_sub_match.start()].strip()
        article_body = article_text[first_sub_match.start():].strip()

    else:
        lines = article_text.split('\n', 1)
        if len(lines) > 1:
            article_title = lines[0].strip()
            article_body = lines[1].strip()
        else:
            article_title = ""
            article_body = article_text

    article_title = ' '.join(article_title.split())

    sub_pattern = rf'({escaped_num}(?:\.\d+)+)\.\s+(.*?)(?={escaped_num}(?:\.\d+)+\.|$)'
    sub_matches = list(re.finditer(sub_pattern, article_body, re.DOTALL))

    if sub_matches:

        if article_title:
            main_article_text = f"{article_number}. {article_title}"
            laws[article_number] = main_article_text

        for sub_match in sub_matches:
            sub_number = sub_match.group(1)
            sub_text = sub_match.group(2).strip()

            sub_text = ' '.join(sub_text.split())
            full_text_with_number = f"{sub_number}. {sub_text}"

            laws[sub_number] = full_text_with_number
    else:
        if article_title:
            full_text = f"{article_number}. {article_title} {article_body}"
        else:
            full_text = f"{article_number}. {article_body}"

        full_text = ' '.join(full_text.split())
        laws[article_number] = full_text

# Save to JSON
with open("Desktop\\inzibati xetalar mecellesi output FINAL.json", "w",
          encoding="utf-8") as output:
    json.dump(laws, output, ensure_ascii=False, indent=2)

print("Done!")
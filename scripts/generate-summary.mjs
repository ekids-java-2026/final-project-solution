import { appendFileSync, existsSync, readdirSync, readFileSync } from "node:fs";
import { join } from "node:path";

const reportDirectory = join("target", "surefire-reports");
const results = new Map();

if (existsSync(reportDirectory)) {
  for (const file of readdirSync(reportDirectory).filter(name => name.startsWith("TEST-") && name.endsWith(".xml"))) {
    const xml = readFileSync(join(reportDirectory, file), "utf8");
    const testcasePattern = /<testcase\b([^>]*)>/g;
    let match;

    while ((match = testcasePattern.exec(xml)) !== null) {
      const attributes = match[1] ?? "";
      const selfClosing = attributes.trimEnd().endsWith("/");
      const closingIndex = selfClosing ? -1 : xml.indexOf("</testcase>", testcasePattern.lastIndex);
      const body = closingIndex < 0 ? "" : xml.slice(testcasePattern.lastIndex, closingIndex);
      const name = attributes.match(/\bname="([^"]+)"/)?.[1];
      const scoreData = name?.match(/^(PB|P)(\d+)_([A-Z]+)_/);

      if (name && scoreData) {
        results.set(name, {
          bonus: scoreData[1] === "PB",
          points: Number(scoreData[2]),
          category: scoreData[3],
          passed: !/<(?:failure|error)\b/.test(body),
        });
      }

      if (closingIndex >= 0) {
        testcasePattern.lastIndex = closingIndex + "</testcase>".length;
      }
    }
  }
}

const categories = [
  ["TYPES", "enum-ები და interface", 20],
  ["MODEL", "აბსტრაქტული კლასი და ინკაფსულაცია", 20],
  ["STATE", "მდგომარეობების გადასვლები", 30],
  ["POLYMORPHISM", "შვილობილი კლასები და override", 20],
  ["TRACKING", "Trackable polymorphism", 10],
];

const requiredResults = [...results.values()].filter(result => !result.bonus);
const bonusResults = [...results.values()].filter(result => result.bonus);
const requiredScore = requiredResults
  .filter(result => result.passed)
  .reduce((sum, result) => sum + result.points, 0);
const bonusScore = bonusResults
  .filter(result => result.passed)
  .reduce((sum, result) => sum + result.points, 0);

const lines = [
  "# ჭკვიანი მიტანის სავარჯიშოს შედეგი",
  "",
  `## ${requiredScore}/100${bonusScore > 0 ? ` + ${bonusScore} bonus` : ""}`,
  "",
  "| თემა | შედეგი | მაქსიმუმი |",
  "|---|---:|---:|",
];

for (const [key, label, maximum] of categories) {
  const score = requiredResults
    .filter(result => result.category === key && result.passed)
    .reduce((sum, result) => sum + result.points, 0);
  lines.push(`| ${label} | ${score} | ${maximum} |`);
}

lines.push(`| **ძირითადი ქულა** | **${requiredScore}** | **100** |`);
lines.push(`| არჩევითი bonus | ${bonusScore} | 10 |`, "");

const failedTests = [...results.entries()].filter(([, result]) => !result.passed);
if (failedTests.length > 0) {
  lines.push("### ჯერ შესასრულებელი შემოწმებები", "");
  for (const [name] of failedTests) {
    lines.push(`- \`${name}\``);
  }
  lines.push("");
} else if (results.size > 0) {
  lines.push("ყველა აღმოჩენილი შემოწმება წარმატებით დასრულდა. 🎉", "");
} else {
  lines.push("ტესტის ანგარიში ვერ მოიძებნა. ჯერ გაუშვი Maven-ის ტესტები.", "");
}

const summary = `${lines.join("\n")}\n`;
const summaryFile = process.env.GITHUB_STEP_SUMMARY;

if (summaryFile) {
  appendFileSync(summaryFile, summary, "utf8");
} else {
  process.stdout.write(summary);
}

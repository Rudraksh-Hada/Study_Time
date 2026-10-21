#!/usr/bin/env python3
"""
Builds the taunt files used by the app:
  app/src/main/assets/taunts_hinglish.txt  (~6.0 MB)  -> picked 56% of the time
  app/src/main/assets/taunts_hindi.txt     (~2.4 MB)  -> picked 24% of the time
  app/src/main/assets/taunts_english.txt   (~2.1 MB)  -> picked 20% of the time

Every line = opener + middle + closer (+ small emoji tag), all hand-written below.
Add / edit lines in the lists and re-run:  python3 tools/generate_taunts.py
"""
import os, random

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "app", "src", "main", "assets")
MB = 1024 * 1024
TAGS = [" 📚", " ⏰", " 😤", " 💔", " 🔥", ""]

# ---------------- HINGLISH (gender-neutral wording on purpose) ----------------
HI_OPEN = [
    "Arre sun toh.", "Yaar, seriously?", "Mummy ko kya jawab doge?", "Papa ne fees bhari thi, yaad hai?",
    "Phone abhi bhi haath mein?", "Ek baat bolun?", "Exam tumhara wait nahi karega.", "Oye hero!",
    "Topper banne ka plan tha na?", "Reels kal bhi mil jayengi.", "Syllabus bechara ro raha hai.",
    "Tumhare dost abhi padh rahe hain.", "Ghadi ki taraf dekho zara.", "Bade sapne dekhe the na?",
    "Ek second ruko.", "Kitaab tumhe miss kar rahi hai.", "Aaj ka din bhi nikal gaya toh?",
    "Sach bolun? Dar lag raha hai mujhe.", "Kal wali tension bhool gaye?", "Rank list mein naam dhundhoge kabhi?",
    "Pichli baar bhi yahi bola tha.", "Dekho, main tumhara dushman nahi hoon.", "Chalo bhai, drama band.",
    "Result wale din kya bologe?", "Itni mehnat se yahan tak aaye ho.", "Yeh 10 minute bhi gaye.",
    "Kasam se, thodi der padh lo.", "Arre wah, phir se break?", "Sunte ho? Kitaab khol lo.", "Bhai, sachchi baat bolun?",
]
HI_MID = [
    "Jo log abhi padh rahe hain, wohi kal seat le jayenge.", "Aaj ki laziness kal ka pachtawa banegi.",
    "Ek chapter khatam karne mein bas 20 minute lagte hain.", "Time nikal raha hai aur syllabus wahin ka wahin hai.",
    "Ghar waale tumse bahut umeed lagaye baithe hain.", "Mehnat ka koi shortcut nahi hota.",
    "Cutoff kisi ka intezaar nahi karta.", "Bahaane banana bahut aata hai, padhna kab aayega?",
    "Phone ki battery full hai, par mehnat 0% pe hai.", "Maa-baap ne apni ichchhayein tumhare liye rok rakhi hain.",
    "Jo aaj chhod diya, wo exam hall mein yaad aayega.", "Revision nahi karoge toh sab kuch bhool jaoge.",
    "Tumhara future tumhare scroll se bada hai.", "Kal se shuru karna sabse purana bahana hai.",
    "Neend baad mein bhi aa jayegi, rank wapas nahi aayegi.", "Dosto ko impress karna hai toh marks se karo.",
    "Itna talent hai, bas use kar lo.", "Mehnat ka phal der se milta hai, par milta zaroor hai.",
    "Study table tumhe akela dekh kar udaas hai.", "Aaj thoda padh loge toh kal kam pachtaoge.",
    "Ek bhi question solve nahi kiya abhi tak?", "Jis din rank aayegi, us din aaj yaad aayega.",
    "Dream college ka gate tumhe bula raha hai.", "Distraction ka koi end nahi hai, bas tumhe rukna padega.",
    "Tumhare notes dhool kha rahe hain.", "Itne saare chapters baaki hain aur tum yahan ho?",
    "Apni race khud jeeto, doosron ko dekhna band karo.", "Dhokha kisi aur ko nahi, khud ko de rahe ho.",
    "Padhai ka maza sirf padhne ke baad aata hai.", "Jab pass hoge, tab yeh taunt yaad karke hasoge.",
    "Aaj ka sacrifice kal ki party hai.", "Thodi si discipline, poori zindagi ka fayda.",
    "Har minute ki keemat hai, mat barbaad karo.", "Backlog ka pahaad chhota karte jao, bada mat banao.",
    "Tumhare andar ek achha student baitha hai, use bahar nikalo.", "Aaj ki mehnat kal ki muskaan hai.",
]
HI_CLOSE = [
    "Abhi kitaab kholo.", "Phone side mein rakho aur shuru karo.", "Sirf 10 minute do, phir dekhna.",
    "Chalo, ek question solve karo.", "Pen uthao, shuru karo.", "Pehla page abhi palto.",
    "Paani piyo aur seat pe baitho.", "Notification band karo, kitaab kholo.", "Main yahin hoon, tum shuru karo.",
    "Ab bina bahane ke padho.", "Ek chapter aaj hi khatam karo.", "Utho, haath-munh dho, desk pe aao.",
    "Dil se bolo, ab padhoge na?", "Aaj ka target poora karke dikhao.", "Kal ka intezaar mat karo, abhi karo.",
    "Mujhe galat saabit karke dikhao.", "Chalo, 25 minute ka focus lagao.", "Shuru karo, baaki sab apne aap ho jayega.",
    "Taunt nahi, pyaar se keh raha hoon: padho.", "Ab bas, padhai pe wapas aao.", "Tumse ho jayega, bas shuru karo.",
    "Mummy-papa ko garv mehsoos karwao.", "Sone se pehle ek achha session karo.", "Dhyaan lagao, duniya baad mein.",
    "Apne aap se kiya wada nibhao.", "Timer lagao aur bhid jao.", "Padhai karo, warna main 10 minute baad phir aaunga.",
    "Jo hua so hua, abhi se shuru.",
]

# ---------------- HINDI (Devanagari) ----------------
HD_OPEN = [
    "सुनो ज़रा।", "अरे यार, सच में?", "माँ-पापा को क्या जवाब दोगे?",
    "फ़ोन अभी भी हाथ में?", "घड़ी की तरफ़ देखो ज़रा।", "सपने बड़े देखे थे ना?",
    "एक पल के लिए सोचो।", "पिछली बार भी यही हुआ था।", "परीक्षा का इंतज़ार नहीं करती।",
    "टॉपर बनने का इरादा था ना?", "रील्स कल भी मिलेंगी।", "किताब तुम्हें बुला रही है।",
    "आज का दिन भी निकल गया?", "सच कहूँ तो डर लग रहा है।", "कल वाली टेंशन भूल गए?",
    "रैंक लिस्ट में नाम ढूँढोगे कभी?", "दोस्त अभी पढ़ रहे हैं।", "बड़े ख़्वाब देखे थे ना?",
    "एक सवाल पूछूँ?", "रुको एक पल।",
]
HD_MID = [
    "आज का आलस कल का पछतावा बनेगा।", "जो अभी पढ़ रहे हैं, वही कल सीट ले जाएँगे।",
    "मेहनत का कोई शॉर्टकट नहीं होता।", "समय निकल रहा है और सिलेबस वहीं का वहीं है।",
    "घरवाले तुमसे बहुत उम्मीद लगाए बैठे हैं।", "कट-ऑफ़ किसी का इंतज़ार नहीं करती।",
    "बहाने बनाना बहुत आता है, पढ़ना कब आएगा?", "फ़ोन की बैटरी फुल है, मेहनत ज़ीरो।",
    "जो आज छोड़ा, वो परीक्षा हॉल में याद आएगा।", "रिवीज़न नहीं किया तो सब भूल जाओगे।",
    "तुम्हारा भविष्य स्क्रॉल से बड़ा है।", "'कल से शुरू' सबसे पुराना बहाना है।",
    "नींद बाद में भी आएगी, रैंक वापस नहीं आएगी।", "इतना टैलेंट है, बस उसे काम में लाओ।",
    "आज की मेहनत कल की मुस्कान है।", "एक भी सवाल हल नहीं किया अभी तक?",
    "ड्रीम कॉलेज का गेट तुम्हें बुला रहा है।", "नोट्स धूल खा रहे हैं।",
    "अपनी रेस खुद जीतो, दूसरों को देखना बंद करो।", "आज की क़ुर्बानी कल की पार्टी है।",
    "हर मिनट क़ीमती है, बर्बाद मत करो।", "अंदर एक अच्छा स्टूडेंट बैठा है, उसे बाहर निकालो।",
    "धोखा किसी और को नहीं, खुद को दे रहे हो।", "पढ़ाई का मज़ा सिर्फ़ पढ़ने के बाद आता है।",
    "थोड़ी सी डिसिप्लिन, पूरी ज़िंदगी का फ़ायदा।",
]
HD_CLOSE = [
    "अभी किताब खोलो।", "फ़ोन किनारे रखो और शुरू करो।",
    "बस दस मिनट दो, फिर देखना।", "अब बिना बहाने के पढ़ो।",
    "एक सवाल अभी हल करो।", "पेन उठाओ, शुरू करो।",
    "पहला पन्ना अभी पलटो।", "नोटिफ़िकेशन बंद करो, किताब खोलो।",
    "आज का टारगेट पूरा करके दिखाओ।", "कल का इंतज़ार मत करो, अभी करो।",
    "मुझे ग़लत साबित करके दिखाओ।", "चलो, 25 मिनट का फ़ोकस लगाओ।",
    "माँ-पापा को गर्व महसूस कराओ।", "टाइमर लगाओ और जुट जाओ।",
    "जो हुआ सो हुआ, अभी से शुरू।", "पढ़ाई करो, वरना मैं 10 मिनट बाद फिर आऊँगा।",
    "ध्यान लगाओ, दुनिया बाद में।", "अपने आप से किया वादा निभाओ।",
    "शुरू करो, बाकी सब अपने आप हो जाएगा।", "अब बस, पढ़ाई पर वापस आओ।",
]

# ---------------- ENGLISH ----------------
EN_OPEN = [
    "Hey, listen for a second.", "Still scrolling?", "Be honest with me.", "Your future self is watching.",
    "Your parents believe in you.", "Tick, tock.", "Remember why you started?", "Another ten minutes gone.",
    "Your competition isn't resting.", "No pressure, but the exam is coming.", "Nice break. Now it's over.",
    "Quick reality check.", "The syllabus won't finish itself.", "Your books are feeling ignored.",
    "Dreams don't study on their own.", "Hmm, that phone looks comfortable in your hand.",
    "Plot twist: you actually have to study.", "I'm not mad, just disappointed.", "Look at the clock.",
    "Comfort is lovely, until results day.",
]
EN_MID = [
    "Someone out there is studying for the seat you want.", "Procrastination feels good now and terrible later.",
    "Every page you skip becomes a question you can't answer.", "Motivation is a myth, discipline is the real thing.",
    "Your goals won't reach you while you sit still.", "Today's effort is tomorrow's confidence.",
    "You've got the brains, you just need the habit.", "The best time to start was earlier; the second best is now.",
    "Results don't care about your excuses.", "The pile of unfinished chapters is growing.",
    "Rest is earned, not assumed.", "One focused hour beats five distracted ones.",
    "Your family's sacrifices are not a joke.", "A little progress today beats a perfect plan tomorrow.",
    "Your notes are collecting dust.", "Nobody ever regretted studying too early.",
    "Success is a pile of small, boring sessions.", "That rank isn't going to improve on its own.",
    "Tomorrow-you is already tired of today-you's excuses.", "You are one good session away from feeling proud.",
    "Distractions will always be there; this exam won't wait.", "Your dream college didn't get easier overnight.",
    "Think of how good it'll feel to finish this chapter.", "The people who made it were once exactly where you are.",
    "Skipping today makes tomorrow heavier.", "Your potential is wasted if it stays in your head.",
    "Stop negotiating with yourself.", "Your streak is waiting for another day.",
    "Think about the relief you'll feel after finishing.", "Hard work stays quiet until the results speak.",
]
EN_CLOSE = [
    "Open the book now.", "Put the phone down and begin.", "Give me just ten minutes.",
    "Solve one question right now.", "Pick up your pen.", "Start with the easiest topic.",
    "Set a timer and begin.", "Close the extra tabs.", "Take a sip of water and sit down.",
    "Do it for the person you want to become.", "Prove me wrong.", "One chapter, right now.",
    "You've got this. Go.", "Begin before you feel ready.", "Do the hard part first.",
    "Make today count.", "Back to the desk, champion.", "Silence the noise and focus.",
    "Future you says thanks in advance.", "Just start. Momentum will do the rest.",
]

def build(o, m, c, target, name, seed):
    lines = [f"{a} {b} {d}{t}" for a in o for b in m for d in c for t in TAGS]
    random.Random(seed).shuffle(lines)
    path = os.path.join(OUT, name)
    size = n = 0
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        for ln in lines:
            f.write(ln + "\n")
            size += len(ln.encode("utf-8")) + 1
            n += 1
            if size >= target:
                break
    print(f"{name}: {n:,} unique lines, {size/MB:.2f} MiB (pool: {len(lines):,})")

os.makedirs(OUT, exist_ok=True)
build(HI_OPEN, HI_MID, HI_CLOSE, int(6.0 * MB), "taunts_hinglish.txt", 11)
build(HD_OPEN, HD_MID, HD_CLOSE, int(2.4 * MB), "taunts_hindi.txt", 33)
build(EN_OPEN, EN_MID, EN_CLOSE, int(2.1 * MB), "taunts_english.txt", 22)

import { FaComments, FaEnvelope, FaGamepad, FaMapMarkerAlt, FaPhoneAlt } from "react-icons/fa";
import envelopeArt from "../assets/images/contact-envelope.webp";
import headphonesArt from "../assets/images/gaming-headphones.webp";

const contactMethods = [
  {
    id: "email",
    title: "Email Us",
    value: "support@gamezone.com",
    href: "mailto:support@gamezone.com",
    hint: "We usually respond within 24 hours.",
    Icon: FaEnvelope,
    tone: "cyan",
  },
  {
    id: "chat",
    title: "Live Chat",
    value: "Available In-App",
    hint: "Chat with our support team in real time.",
    Icon: FaComments,
    tone: "purple",
  },
  {
    id: "call",
    title: "Call Us",
    value: "+1 (123) 456-7890",
    href: "tel:+11234567890",
    hint: "Mon – Fri | 10 AM – 6 PM (EST)",
    Icon: FaPhoneAlt,
    tone: "cyan",
  },
  {
    id: "office",
    title: "Our Office",
    value: "123 Game Zone Street, Play City, GC 12345, United States",
    Icon: FaMapMarkerAlt,
    tone: "purple",
  },
];

const Contact = () => {
  return (
    <div className="gz-page-shell gz-page-shell--contact">
      <div className="gz-contact">
        <section className="gz-contact__hero">
          <div>
            <h1 className="gz-contact__title">
              Contact <span className="gz-contact__us">Us</span>
            </h1>
            <div className="gz-divider mt-2 max-w-[200px]">
              <FaGamepad className="text-gz-primary-cyan" size={14} />
            </div>
            <p className="mt-3 max-w-xl text-sm leading-relaxed text-gz-text-secondary">
              We&apos;d love to hear from you! Whether you have a question, feedback or just want to
              say hi, our team is here to help.
            </p>
          </div>

          <div className="gz-contact__hero-art">
            <img src={envelopeArt} alt="Glowing envelope with email, chat, and phone icons" />
          </div>
        </section>

        <section className="gz-contact__grid" aria-label="Contact methods">
          {contactMethods.map(({ id, title, value, href, hint, Icon, tone }) => (
            <article key={id} className="gz-contact-card">
              <span className={`gz-contact-card__icon gz-contact-card__icon--${tone}`}>
                <Icon />
              </span>
              <div className="gz-contact-card__body">
                <h2 className="text-sm font-semibold text-gz-text">{title}</h2>
                {href ? (
                  <a
                    href={href}
                    className={`mt-0.5 text-sm font-medium ${
                      tone === "purple" ? "text-gz-purple-accent" : "gz-link"
                    }`}
                  >
                    {value}
                  </a>
                ) : (
                  <p
                    className={`mt-0.5 text-sm font-medium ${
                      tone === "purple" ? "text-gz-purple-accent" : "text-gz-primary-cyan"
                    }`}
                  >
                    {value}
                  </p>
                )}
                {hint ? (
                  <p className="mt-0.5 text-xs leading-relaxed text-gz-text-secondary">{hint}</p>
                ) : null}
              </div>
            </article>
          ))}
        </section>

        <section className="gz-contact-banner">
          <div className="gz-contact-banner__art">
            <img src={headphonesArt} alt="Glowing GameZone gaming headphones" />
          </div>
          <div className="flex-1">
            <h2 className="gz-contact-banner__heading">We&apos;re here for you!</h2>
            <p className="mt-1.5 text-sm leading-relaxed text-gz-text-secondary">
              Your feedback helps us improve and build a better gaming experience for everyone.
              Thank you for being a part of{" "}
              <span className="gz-text-neon font-semibold">GameZone</span>!
            </p>
          </div>
        </section>
      </div>
    </div>
  );
};

export default Contact;

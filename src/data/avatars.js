import {
  AceArt,
  BlazeArt,
  LunaArt,
  MiraArt,
  NovaArt,
  PixelArt,
  RexArt,
  SageArt,
} from "../components/profile/avatarArt";

export const AVATARS = [
  { id: "NOVA", name: "Nova", Art: NovaArt },
  { id: "BLAZE", name: "Blaze", Art: BlazeArt },
  { id: "LUNA", name: "Luna", Art: LunaArt },
  { id: "PIXEL", name: "Pixel", Art: PixelArt },
  { id: "ACE", name: "Ace", Art: AceArt },
  { id: "SAGE", name: "Sage", Art: SageArt },
  { id: "REX", name: "Rex", Art: RexArt },
  { id: "MIRA", name: "Mira", Art: MiraArt },
];

export const getAvatarById = (id) => AVATARS.find((avatar) => avatar.id === id) || null;

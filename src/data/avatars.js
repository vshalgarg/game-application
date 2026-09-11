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
  { id: "nova", name: "Nova", Art: NovaArt },
  { id: "blaze", name: "Blaze", Art: BlazeArt },
  { id: "luna", name: "Luna", Art: LunaArt },
  { id: "pixel", name: "Pixel", Art: PixelArt },
  { id: "ace", name: "Ace", Art: AceArt },
  { id: "sage", name: "Sage", Art: SageArt },
  { id: "rex", name: "Rex", Art: RexArt },
  { id: "mira", name: "Mira", Art: MiraArt },
];

export const getAvatarById = (id) => AVATARS.find((avatar) => avatar.id === id) || null;

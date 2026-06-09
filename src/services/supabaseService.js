import { supabase } from "../utils/supabaseClient";

// GET DATA
export const getRooms = async () => {
  const { data, error } = await supabase
    .from("rooms")
    .select("*");

  if (error) throw error;
  return data;
};

// INSERT DATA
export const createRoomDB = async (payload) => {
  const { data, error } = await supabase
    .from("rooms")
    .insert([payload])
    .select();

  if (error) throw error;
  return data;
};
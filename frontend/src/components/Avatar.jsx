export default function Avatar({ member, size = 40 }) {
  const style = { width: size, height: size };
  if (member?.profileImage) {
    return <img className="avatar" style={style} src={member.profileImage} alt={member.nickname} />;
  }
  return (
    <div className="avatar avatar-empty" style={{ ...style, fontSize: size * 0.42 }}>
      {member?.nickname?.[0] ?? '?'}
    </div>
  );
}
